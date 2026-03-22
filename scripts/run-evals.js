#!/usr/bin/env node

const { execSync, spawn } = require("child_process");
const fs = require("fs");
const path = require("path");

const EVALS_PATH = "skills-workspace/evals/evals.json";
const WORKSPACE_PATH = "skills-workspace";

function parseArgs(argv) {
  const args = {
    iteration: null,
    evals: null,       // null = all, or array of ids
    baseline: false,
    model: "sonnet",
    parallel: 4,
    gradeOnly: false,
  };

  for (let i = 2; i < argv.length; i++) {
    switch (argv[i]) {
      case "--iteration":
        args.iteration = parseInt(argv[++i], 10);
        break;
      case "--evals":
        args.evals = argv[++i].split(",").map(Number);
        break;
      case "--baseline":
        args.baseline = true;
        break;
      case "--model":
        args.model = argv[++i];
        break;
      case "--parallel":
        args.parallel = parseInt(argv[++i], 10);
        break;
      case "--grade-only":
        args.gradeOnly = true;
        break;
      default:
        console.error(`Unknown argument: ${argv[i]}`);
        process.exit(1);
    }
  }

  if (!args.iteration) {
    console.error("Usage: node scripts/run-evals.js --iteration N [options]");
    console.error("Options:");
    console.error("  --evals 1,2,3       Run specific evals only");
    console.error("  --baseline          Also run without skill");
    console.error("  --model MODEL       Model to use (default: sonnet)");
    console.error("  --parallel N        Max parallel evals (default: 4)");
    console.error("  --grade-only        Re-grade existing outputs");
    process.exit(1);
  }

  return args;
}

async function main() {
  const args = parseArgs(process.argv);
  const repoRoot = execSync("git rev-parse --show-toplevel", {
    encoding: "utf-8",
  }).trim();

  const evalsData = JSON.parse(
    fs.readFileSync(path.join(repoRoot, EVALS_PATH), "utf-8")
  );

  let evals = evalsData.evals;
  if (args.evals) {
    evals = evals.filter((e) => args.evals.includes(e.id));
  }

  console.log(
    `\nEval run: iteration ${args.iteration}, ${evals.length} evals, model ${args.model}`
  );

  const worktreePath = args.gradeOnly ? null : setupWorktree(repoRoot);
  const iterationDir = path.join(
    repoRoot,
    WORKSPACE_PATH,
    `iteration-${args.iteration}`
  );
  fs.mkdirSync(iterationDir, { recursive: true });

  try {
    if (!args.gradeOnly) {
      await generateResponses(evals, worktreePath, iterationDir, args);
    }
    await gradeResponses(evals, iterationDir, args);
    const benchmark = aggregateResults(evals, iterationDir, args);
    const previousBenchmark = loadPreviousBenchmark(repoRoot, args.iteration);
    generateReport(benchmark, previousBenchmark, iterationDir, args);
    console.log("\nDone. Results in:", iterationDir);
  } finally {
    if (worktreePath) {
      cleanupWorktree(worktreePath);
    }
  }
}

main().catch((err) => {
  console.error("Fatal error:", err.message);
  process.exit(1);
});
