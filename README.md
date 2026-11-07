# FeatureRef

FeatureRef (OrganicRef) analyzes Java projects and recommends refactorings. It can run a recommender, build a topic model, or analyze results from previous runs.

## Requirements

- JDK 11 or newer (the source uses Java features newer than Java 8).
- Git, for cloning a project and for the `LOCAL_CHANGE` context strategy.
- Internet access on the first build so Gradle can download its distribution and Maven dependencies.
- Allow Gradle enough memory: the application is configured with an 8 GB maximum heap by default.

The Gradle Wrapper is the recommended way to build and run the project. On Windows use `gradlew.bat`; on Linux/macOS use `./gradlew`.

## Build

From the repository root:

```sh
# Windows
gradlew.bat clean build

# Linux or macOS
./gradlew clean build
```

To compile without running tests:

```sh
gradlew.bat classes
./gradlew classes
```

## Command line

Use `gradlew.bat run --args="..."` on Windows or `./gradlew run --args="..."` on Linux/macOS. Paths containing spaces should be quoted inside the `--args` value. Do not put a command word such as `organic` before the options.

```sh
gradlew.bat run --args="-src \"C:\\work\\spring-petclinic\" -out \"results\\petclinic\" -alg BASELINE"
./gradlew run --args='-src /work/spring-petclinic -out results/petclinic -alg BASELINE'
```

| Option | Values / default | Meaning |
| --- | --- | --- |
| `-src`, `--source-folder` | Required | Source project directory for recommendation/topic modeling, or results directory for analysis. |
| `-out`, `--output-folder` | Required | Directory where generated files are written. Created automatically. |
| `-alg`, `--optimization-algorithm` | `SA` (default); `BASELINE_NF`, `BASELINE`, `NSGAII`, `NSGAIII`, `RANDOM`, `SA`, `MOSA`, `AMOSA`, `MOSAHybridNSGAII` | Recommendation algorithm. |
| `-con`, `--context-strategy` | `SMELLS` (default); `SMELLS`, `LOCAL_CHANGE` | Selects degraded types or types with Java changes from Git history as context. |
| `-mev`, `--max-evaluations` | `1000` | Evaluation/iteration limit for algorithms that use it. |
| `-ps`, `--population-size` | `50` | Population size for population based algorithms. |
| `-cs`, `--cooling-schedule` | `EXPONENTIAL` (default); `EXPONENTIAL`, `LINEAR`, `BOLTZ` | Cooling schedule for `SA`. |
| `-tm`, `--topic-model-mode` | Flag | Build and save a topic model for the source project, without running recommendations. |
| `-cmf`, `--concerns-model-dir` | Optional directory | Reuse `topics-model.gz` and `inferencer.mallet` from an existing topic model directory. |
| `-am`, `--analysis-mode` | `SP` or `MP` | Analyze previously generated JSON results: `SP` for one result tree, `MP` for each project folder under `-src`. |

### Recommendation algorithms

Run the default simulated annealing algorithm (`SA`):

```sh
./gradlew run --args='-src /path/to/java-project -out results/run-sa'
```

Choose an algorithm explicitly and tune the relevant parameters:

```sh
./gradlew run --args='-src /path/to/java-project -out results/run-nsgaii -alg NSGAII -mev 1000 -ps 50'
./gradlew run --args='-src /path/to/java-project -out results/run-nsgaiii -alg NSGAIII -mev 1000 -ps 50'
./gradlew run --args='-src /path/to/java-project -out results/run-random -alg RANDOM -mev 1000'
./gradlew run --args='-src /path/to/java-project -out results/run-baseline -alg BASELINE'
./gradlew run --args='-src /path/to/java-project -out results/run-baseline-no-features -alg BASELINE_NF'
./gradlew run --args='-src /path/to/java-project -out results/run-mosa -alg MOSA -mev 1000 -ps 50'
./gradlew run --args='-src /path/to/java-project -out results/run-amosa -alg AMOSA -mev 1000 -ps 50'
./gradlew run --args='-src /path/to/java-project -out results/run-mosa-hybrid -alg MOSAHybridNSGAII -mev 1000 -ps 50'
./gradlew run --args='-src /path/to/java-project -out results/run-sa-linear -alg SA -mev 1000 -cs LINEAR'
```

Context selection applies to recommendation runs:

```sh
./gradlew run --args='-src /path/to/git-project -out results/local-change -alg BASELINE -con LOCAL_CHANGE'
./gradlew run --args='-src /path/to/java-project -out results/smells -alg BASELINE -con SMELLS'
```

`LOCAL_CHANGE` reads Java file paths from the project's Git history (`git log --all`). Use a Git checkout with commits and branches available locally.

### Topic modeling

Create a topic model and its supporting files in the output directory:

```sh
./gradlew run --args='-src /path/to/java-project -out results/topic-model -tm'
```

The output includes `topics-model.gz`, `inferencer.mallet`, `top-words.txt`, and `diagnostic.xml`. To reuse an existing model, provide its containing directory with `-cmf` on a recommendation run:

```sh
./gradlew run --args='-src /path/to/java-project -out results/reuse-model -alg BASELINE -cmf results/topic-model'
```

### Analyze saved results

Analysis reads JSON files from a prior recommendation run and writes tables to `-out`:

```sh
# One result tree
./gradlew run --args='-src results/run-sa -out results/analysis-sa -am SP'

# Multiple project result trees (one project directory per child of -src)
./gradlew run --args='-src results/experiments -out results/analysis-all -am MP'
```

The generated files include `analysis-table.csv` and `statistical-tests.txt`; non-dominated solutions are also written for each release. `MP` is the default analysis mode when `-am` is omitted.

## Output files

Recommendation runs save timestamped JSON files for the full result, simplified result, context impact, and (for algorithms that expose progress) fitness snapshots. Topic modeling writes the model files listed above. Keep run output directories separate so analysis can distinguish experiments. The repository ignores the top-level `results/` directory.

## Example: Spring Petclinic

```sh
git clone https://github.com/spring-projects/spring-petclinic.git
./gradlew run --args='-src spring-petclinic -out results/spring-petclinic -alg BASELINE'
```

On Windows, replace `./gradlew` with `gradlew.bat` and adapt the source path. To use a search algorithm, replace `BASELINE` with any supported value from the algorithm list.

### First run in this checkout

The initial Petclinic run was completed with the `BASELINE` algorithm. The shallow checkout is at `results/input-spring-petclinic`, the generated files are at `results/spring-petclinic`, and the console log is at `results/first-run.log`. The run completed successfully and produced timestamped context/result JSON, a simplified result JSON, and the topic-model files. To repeat it from the repository root:

```sh
gradlew.bat run --args="-src results/input-spring-petclinic -out results/spring-petclinic -alg BASELINE"
./gradlew run --args='-src results/input-spring-petclinic -out results/spring-petclinic -alg BASELINE'
```
