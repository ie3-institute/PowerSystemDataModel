(markov-load-model)=

# Markov-based Load Profiles

Markov-based load profiles allow stochastic load simulation using Markov chains combined with Gaussian Mixture Models
(GMMs). The models are produced by an external Python trainer
([simonaMarkovLoad](https://github.com/ie3-institute/simonaMarkovLoad)) and consumed by PSDM via JSON files.

## Overview

Each Markov model encodes:

- A set of **discrete states** representing different load levels
- **Transition matrices** per time bucket that describe the probability of moving from one state to another
- **GMM parameters** per state and bucket that allow sampling a continuous normalized power value within a state

During simulation, one step works as follows:

1. **Bucket lookup** - determine the current time bucket from month, weekday/weekend and quarter-hour
2. **State resolution** - use the previous state (or discretize the initial value for the first step)
3. **Seed derivation** - compute a deterministic RNG seed for reproducibility
4. **Transition + sampling** - draw the next state from the transition row, then sample a normalized value from the GMM
5. **Denormalization** - scale the normalized value back to physical power using {code}`minPower` and {code}`maxPower`

## File Naming

Markov model files follow the naming convention {code}`markov_<profileKey>.json`, e.g. {code}`markov_h0.json`.
The profile key must be unique across all load-profile sources. Do not use the same key for both a CSV profile
({code}`lpts_<key>.csv`) and a Markov profile ({code}`markov_<key>.json`).

## JSON Schema

The JSON file uses the schema identifier {code}`simonaMarkovLoad:psdm:1.0` and has the following top-level structure:

```text
{
  "schema": "simonaMarkovLoad:psdm:1.0",
  "generated_at": "2025-01-15T12:00:00Z",
  "generator": { ... },
  "time_model": { ... },
  "value_model": { ... },
  "parameters": { ... },
  "data": { ... }
}
```

### generator

Metadata about the tool that produced the model.

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - name
     - String
     - Name of the trainer (e.g. {code}`simonaMarkovLoad`)
   * - version
     - String
     - Trainer version
   * - config
     - Object
     - Key-value pairs of the training configuration
```

### time_model

Describes how time is mapped to buckets.

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - bucket_count
     - Integer
     - Total number of time buckets (typically 2304 = 12 months x 2 day types x 96 quarter-hours)
   * - bucket_encoding
     - Object
     - Contains a {code}`formula` field documenting the bucket index calculation
   * - sampling_interval_minutes
     - Integer
     - Time step length in minutes (typically 15)
   * - timezone
     - String
     - IANA timezone identifier (e.g. {code}`Europe/Berlin`)
```

The bucket index is computed as:

```
bucket = month * 192 + isWeekend * 96 + quarterHour
```

where {code}`isWeekend` is 1 for Saturday/Sunday and 0 otherwise, and {code}`quarterHour` is the 0-based quarter-hour
of the day (0..95).

### value_model

Defines the value space and normalization.

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - value_unit
     - String
     - Physical unit of the original training data (e.g. {code}`W`)
   * - normalization
     - Object
     - See below
   * - discretization
     - Object
     - See below
```

#### normalization

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - method
     - String
     - Normalization strategy used during training (e.g. {code}`minmax_per_series`)
   * - max_power
     - Object
     - Upper bound: {code}`{"value": <number>, "unit": "kW"}`
   * - min_power
     - Object
     - Lower bound: {code}`{"value": <number>, "unit": "kW"}`
```

Denormalization in PSDM uses these bounds as:

```
power = minPower + normalizedValue * (maxPower - minPower)
```

Negative {code}`min_power` values are valid and represent net feed-in (e.g. PV households).

#### discretization

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - states
     - Integer
     - Number of discrete Markov states
   * - thresholds_right
     - Array of Double
     - Right-edge thresholds for mapping normalized values to states (length = states - 1)
```

### parameters

Optional metadata about how the trainer produced transitions and GMMs.

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - transitions
     - Object
     - Contains {code}`empty_row_strategy`: how the trainer handled transition rows with no observations
   * - gmm
     - Object
     - Contains {code}`value_col` (column name), {code}`verbose` (logging level), and
       {code}`heartbeat_seconds` (trainer watchdog interval, not used at runtime)
```

### data

Contains the actual model data: transition probabilities and GMM parameters.

#### data.transitions

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - dtype
     - String
     - Data type (e.g. {code}`float64`)
   * - encoding
     - String
     - Encoding format (e.g. {code}`dense`)
   * - shape
     - Array of Integer
     - Dimensions: {code}`[bucket_count, states, states]`
   * - values
     - 3D Array of Double
     - Transition probabilities: {code}`values[bucket][fromState][toState]`
```

Each row {code}`values[bucket][state]` is a probability distribution over the next states (sums to 1.0).

#### data.gmms

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - buckets
     - Array of Bucket
     - One entry per time bucket, each containing a {code}`states` array
```

Each bucket contains a {code}`states` array with one entry per Markov state. Each state entry is either {code}`null`
(if no GMM was fitted for that state/bucket combination) or an object with:

```{list-table}
   :widths: auto
   :header-rows: 1

   * - Field
     - Type
     - Description
   * - weights
     - Array of Double
     - Component weights (sum to 1.0)
   * - means
     - Array of Double
     - Component means in normalized [0, 1] space
   * - variances
     - Array of Double
     - Component variances
```

During sampling, a GMM component is drawn according to {code}`weights`, then a value is sampled from the corresponding
Gaussian and clamped to [0, 1] before denormalization.

## Loading and Validation

Markov models are loaded via {code}`JsonMarkovProfileSource`, which:

- Reads the JSON file lazily on first access (not at construction time)
- Caches the parsed {code}`MarkovLoadModel` for subsequent calls
- Validates all 20 mandatory fields via {code}`DataSource.validate()` before use
- Is thread-safe ({code}`synchronized` lazy loading)
