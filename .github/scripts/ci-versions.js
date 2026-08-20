'use strict';

// Schema of .github/ci-versions.json, shared by compose-legs.js and resolve-versions.js.

const fs = require('fs');

// Relative, not $GITHUB_WORKSPACE-prefixed: that variable is backslash-separated on Windows runners.
const DECLARATION = '.github/ci-versions.json';

function readDeclaration(file = DECLARATION) {
  return JSON.parse(fs.readFileSync(file, 'utf8'));
}

// Nothing here deduplicates or reorders: compose-legs.js rejects duplicates by name, and one rule in
// one place cannot disagree with itself. Declaration order is what the job list shows.

function ideaVersions(declaration) {
  return [declaration.idea.minimumSupported, ...(declaration.idea.additionalToTest || [])];
}

// Null rather than a default: a wrong JBR level fails the compile, so a guess is worse than stopping.
function javaFor(declaration, version) {
  const idea = ideaVersions(declaration).find((entry) => entry.version === version);
  return (idea && idea.java) || null;
}

// The one supported pair. Must equal the mise.toml pin.
function baseline(declaration) {
  return declaration.beam.baseline;
}

// Versions being widened towards support; continue-on-error marks one still expected to fail.
function beamAdditional(declaration) {
  return declaration.beam.additional || [];
}

function verifyProducts(idea) {
  return idea.verify || [];
}

module.exports = {
  DECLARATION,
  readDeclaration,
  ideaVersions,
  javaFor,
  baseline,
  beamAdditional,
  verifyProducts,
};
