'use strict';

// Fails a verification leg when the plugin uses an internal platform API absent from
// .github/internal-api-usages-allowlist.txt. Anything not on that list blocks publishing, so this is a
// release ceiling, not a lint - hence a hard failure.
//
// The verifier cannot do it: INTERNAL_API_USAGES is all-or-nothing and it has no per-usage allowlist, so
// the gate diffs the verifier's own report. Deliberately thin - whether that report is complete is the
// verifier's business, not this script's.

const fs = require('fs');
const path = require('path');
const { addSummary, fail, warn } = require('./actions');

// A literal, not path.join: it is printed back in error messages, and path.join backslashes it on Windows.
const DEFAULT_ALLOWLIST = '.github/internal-api-usages-allowlist.txt';
const USAGES_FILE = 'internal-api-usages.txt';

// Split on this rather than the whole sentence: verifier-version is LATEST, so only its opening is stable.
const BOILERPLATE = '. This ';

// The platform symbol alone. Anchored on the connector verb so the field form keeps its `X : T`.
const SYMBOL = /^(Internal (?:class|interface|method|field) .+?) is (?:referenced|invoked|accessed|overridden) in /;

const SKIP_DIRS = new Set(['.git', '.gradle', 'node_modules', '.idea']);
const MAX_DEPTH = 8;

// A report line reduced to a key. Both sides go through this, so a raw line can be pasted in verbatim.
function normalize(line) {
  const marker = line.lastIndexOf(BOILERPLATE);
  if (marker === -1) return null;

  return (
    line
      .slice(0, marker)
      // Parameter names come from debug info and drift between builds; types do not.
      .replace(/\(([^()]*)\)/g, (_match, args) => {
        const types = args
          .split(',')
          .map((arg) => arg.trim().split(/\s+/)[0])
          .filter(Boolean);
        return `(${types.join(', ')})`;
      })
      .replace(/\s+/g, ' ')
      .trim()
  );
}

function platformSymbol(key) {
  const match = SYMBOL.exec(key);
  return match ? match[1] : null;
}

function readAllowlist(file) {
  let text;
  try {
    text = fs.readFileSync(file, 'utf8');
  } catch (error) {
    fail(
      `could not read the internal API allowlist at ${file}: ${error.message}. It is the record of which ` +
        'internal platform APIs JetBrains have agreed to ignore in the plugin\'s Marketplace verification, ' +
        'so verification cannot be gated without it.',
      'Internal API allowlist missing'
    );
  }

  const allowed = new Map();
  const symbols = new Set();

  text.split('\n').forEach((raw, index) => {
    const line = raw.trim();
    if (!line || line.startsWith('#')) return;

    const key = normalize(line) || line.replace(/\s+/g, ' ');
    allowed.set(key, index + 1);
    const symbol = platformSymbol(key);
    if (symbol) symbols.add(symbol);
  });

  if (allowed.size === 0) {
    warn(
      `${file} lists no approved usages, so every internal API usage the verifier reports will fail.`,
      'Internal API allowlist is empty'
    );
  }

  return { allowed, symbols };
}

function isDirectory(candidate) {
  try {
    return fs.statSync(candidate).isDirectory();
  } catch {
    return false;
  }
}

function findReports(root, depth = 0) {
  if (depth > MAX_DEPTH) return [];

  let entries;
  try {
    entries = fs.readdirSync(root, { withFileTypes: true });
  } catch {
    return [];
  }

  const found = [];
  for (const entry of entries) {
    const full = path.join(root, entry.name);
    if (entry.isDirectory()) {
      if (!SKIP_DIRS.has(entry.name)) found.push(...findReports(full, depth + 1));
    } else if (entry.name === USAGES_FILE) {
      found.push(full);
    }
  }
  return found;
}

function main() {
  const allowlistFile = process.env.ALLOWLIST || DEFAULT_ALLOWLIST;
  const { allowed, symbols } = readAllowlist(allowlistFile);
  const leg = process.env.LEG || 'this leg';
  // `=== 'success'`, not `!== 'failure'`: a skipped or cancelled verify wrote no reports to demand.
  const verified = process.env.VERIFY_OUTCOME === 'success';

  // The action sed's this out of the verifier's console log, so it can be empty or container-absolute.
  // No fallback to searching the workspace on purpose: one that finds nothing would pass the leg having
  // checked nothing.
  const root = process.env.REPORTS_DIR;
  if (!root || !isDirectory(root)) {
    const where = root ? `"${root}", which is not a directory here` : 'nothing';
    if (verified) {
      fail(
        `the verifier action reported its reports directory as ${where}, so no internal API usage could be ` +
          'checked. Verification succeeded, so those reports exist and this gate is broken - passing the leg ' +
          'would mean nothing. Upload Verify Reports is uploading nothing either; both read the same output.',
        'Internal API usages not checked'
      );
    }
    warn(
      `the verifier action reported its reports directory as ${where}, so no internal API usage was checked. ` +
        'Verification did not succeed, so the reports may legitimately be absent.',
      'Internal API usages not checked'
    );
    return;
  }

  const reports = findReports(root);
  if (reports.length === 0) {
    // The verifier writes this file lazily on its first line, so no file means no usages.
    addSummary(`### Internal API usages\n\nNo internal API usages reported for ${leg}.\n\n`);
    reportStale(allowed, new Set(), allowlistFile);
    return;
  }

  // The verifier reports a SET, and that is what JetBrains counted. Do not "fix" this into a multiset.
  const observed = new Map();
  const malformed = [];

  for (const report of reports) {
    for (const raw of fs.readFileSync(report, 'utf8').split('\n')) {
      const line = raw.trim();
      if (!line) continue;

      const key = normalize(line);
      if (key === null) malformed.push({ report, line });
      else if (!observed.has(key)) observed.set(key, line);
    }
  }

  if (malformed.length > 0) {
    fail(
      `${malformed.length} line(s) in the verifier's ${USAGES_FILE} do not carry the "${BOILERPLATE.trim()} ` +
        '<kind> is marked with ..." sentence this check splits on, so no usage could be compared. The report ' +
        'format has changed - update normalize() in .github/scripts/check-internal-api-usages.js. ' +
        `First line: ${malformed[0].line}`,
      'Verifier report format changed'
    );
  }

  const summary = [
    '### Internal API usages',
    '',
    `${observed.size} reported for ${leg}, across ${reports.length} report file(s).`,
    '',
  ];

  const unexpected = [...observed.keys()].filter((key) => !allowed.has(key));
  if (unexpected.length > 0) {
    const newSymbols = [];
    const movedUsages = [];
    unexpected.forEach((key) => {
      const symbol = platformSymbol(key);
      // An unfamiliar line shape counts as new - the cautious side.
      if (symbol && symbols.has(symbol)) movedUsages.push(key);
      else newSymbols.push(key);
    });

    summary.push('| verdict | usage |', '|---|---|');
    newSymbols.forEach((key) => summary.push(`| **new internal API** | \`${key}\` |`));
    movedUsages.forEach((key) => summary.push(`| moved usage | \`${key}\` |`));
    summary.push('');
    addSummary(`${summary.join('\n')}\n`);

    // Detail to the log, not the annotation: GitHub ends an annotation at the first newline. These keys
    // are the exact form the allowlist stores, so they can be pasted straight in.
    if (newSymbols.length > 0) {
      console.log('Internal platform APIs used without a JetBrains agreement:');
      newSymbols.forEach((key) => console.log(`  ${key}`));
    }
    if (movedUsages.length > 0) {
      console.log('Approved APIs used from plugin code the allowlist does not record:');
      movedUsages.forEach((key) => console.log(`  ${key}`));
    }

    // One fail() for both: it exits, so a second would never print - but the remedies differ.
    const remedies = [];
    if (newSymbols.length > 0) {
      remedies.push(
        `${newSymbols.length} use(s) an internal platform API JetBrains have not agreed to ignore, which BLOCKS ` +
          `publishing - adding a line to ${allowlistFile} does not make it publishable, the ceiling has to be ` +
          'renegotiated with JetBrains first, and removing the usage is the better answer'
      );
    }
    if (movedUsages.length > 0) {
      remedies.push(
        `${movedUsages.length} use(s) an already-approved API from plugin code the allowlist does not record - the ` +
          'ceiling is unchanged, so replace the stale allowlist line with the reported one and say in the pull ' +
          'request that the count is unchanged'
      );
    }

    fail(
      `${unexpected.length} internal API usage(s) are not on the approved list; see the log above for each. ` +
        `${remedies.join('. ')}. The allowlist is shared by every verification leg, so this is not specific to ` +
        'this product or platform version.',
      newSymbols.length > 0 ? 'New internal API usage' : 'Internal API usage moved'
    );
  }

  summary.push('All reported usages are on the approved list.', '');
  addSummary(`${summary.join('\n')}\n`);
  reportStale(allowed, new Set(observed.keys()), allowlistFile);
}

// A warning, not a failure: one allowlist covers every leg, and a version may stop annotating an API in
// one product only.
function reportStale(allowed, observedKeys, allowlistFile) {
  const stale = [...allowed.entries()].filter(([key]) => !observedKeys.has(key));
  if (stale.length === 0) return;

  console.log('Allowlist entries no longer reported:');
  stale.forEach(([key, lineNumber]) => console.log(`  ${allowlistFile}:${lineNumber} ${key}`));

  warn(
    `${stale.length} allowlist entr(ies) are no longer reported; see the log above for each. If the usage is gone ` +
      `for good, remove the line from ${allowlistFile} - a shrinking ceiling is good news, but a stale entry hides ` +
      'the next usage that reuses that API.',
    'Stale internal API allowlist entry'
  );
}

// The opposite of record-leg-status.js, which swallows exceptions so reporting cannot redden a leg. A
// gate that hides its own bug and exits 0 is the green check that lies.
try {
  main();
} catch (error) {
  fail(
    `the internal API allowlist check itself failed: ${error.stack || error.message}`,
    'Internal API check failed'
  );
}
