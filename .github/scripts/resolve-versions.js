'use strict';

// Fills in any version input left empty on .github/actions/setup-env, so callers that do not care
// (release, tag, build-plugin) get the minimum supported platform without restating it. The test legs
// pass every value explicitly and so never read the declaration.

const fs = require('fs');
const { DECLARATION, readDeclaration, javaFor, baseline } = require('./ci-versions');
const { setOutput, fail } = require('./actions');

const setupElixir = process.env.SETUP_ELIXIR === 'true';
let idea = process.env.IDEA_VERSION || '';
let java = process.env.JAVA_VERSION || '';
let elixir = process.env.ELIXIR_VERSION || '';
let otp = process.env.OTP_VERSION || '';

const beamNeeded = setupElixir && (!elixir || !otp);

if (!idea || !java || beamNeeded) {
  if (!fs.existsSync(DECLARATION)) {
    fail(
      `${DECLARATION} not found. setup-env resolves omitted version inputs from it, so check out` +
        ' the repository before calling this action.',
    );
  }

  const declaration = readDeclaration();

  if (!idea) idea = declaration.idea.minimumSupported.version;

  if (!java) {
    java = javaFor(declaration, idea);
    if (!java) {
      fail(
        `no java level declared for IDEA ${idea} in ${DECLARATION}; add it there or pass java-version explicitly.`,
      );
    }
  }

  if (setupElixir) {
    const pair = baseline(declaration);
    if (!elixir) elixir = pair.elixir;
    if (!otp) otp = pair.otp;
  }
}

console.log(`Resolved: IDEA ${idea}, JBR ${java}, Elixir ${elixir || '<skipped>'}, OTP ${otp || '<skipped>'}`);

setOutput('idea-version', idea);
setOutput('java-version', java);
setOutput('elixir-version', elixir);
setOutput('otp-version', otp);
