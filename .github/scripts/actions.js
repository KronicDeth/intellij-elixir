'use strict';

// Not @actions/core: that would make .github/scripts an npm package needing a committed node_modules
// or a bundling step, and with it the "did you rebuild dist/?" failure mode.

const fs = require('fs');

function setOutput(name, value) {
  const text = value === undefined || value === null ? '' : String(value);
  // The runner reads $GITHUB_OUTPUT line by line, so a newline would become the next assignment.
  if (text.includes('\n')) throw new Error(`output ${name} contains a newline`);

  const file = process.env.GITHUB_OUTPUT;
  if (file) fs.appendFileSync(file, `${name}=${text}\n`, 'utf8');
  else console.log(`${name}=${text}`);
}

function addSummary(markdown) {
  const file = process.env.GITHUB_STEP_SUMMARY;
  if (file) fs.appendFileSync(file, markdown, 'utf8');
  else process.stdout.write(markdown);
}

function annotate(level, message, title) {
  console.log(`::${level}${title ? ` title=${title}` : ''}::${message}`);
}

// The annotation is what surfaces in the pull request's Checks tab; a non-zero exit alone does not.
function fail(message, title) {
  annotate('error', message, title);
  process.exit(1);
}

// For a script that must not fail its step, so the annotation is the whole signal.
function warn(message, title) {
  annotate('warning', message, title);
}

module.exports = { setOutput, addSummary, fail, warn };
