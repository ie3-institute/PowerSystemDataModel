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
A profile key consists of 1 to 11 letters, optionally followed by up to 3 digits.
The profile key must be unique across all load-profile sources. Do not use the same key for both a CSV profile
({code}`lpts_<key>.csv`) and a Markov profile ({code}`markov_<key>.json`).

## JSON Schema

The JSON file uses the schema identifier {code}`simonaMarkovLoad:psdm:1.0` and has the following top-level structure.
Note that PSDM only requires the {code}`schema` field to be present; its value is currently not verified.

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

where {code}`month` is the 0-based month (January = 0), {code}`isWeekend` is 1 for Saturday/Sunday and 0 otherwise,
and {code}`quarterHour` is the 0-based quarter-hour of the day (0..95). Timestamps are converted to the model's
timezone before the bucket index is computed.

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
     - Unit of the stored values; the trainer exports {code}`normalized` ([0, 1] scale)
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
     - Normalization strategy used during training (e.g. {code}`minmax_global`)
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

Only {code}`kW` is supported as unit for both bounds, other units are rejected when the model is loaded.
Furthermore, {code}`max_power` must be strictly greater than {code}`min_power`.

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
     - Right-edge thresholds for mapping normalized values to states (length = states - 1). Bins are
       left-closed and right-open: a value exactly on a threshold maps to the higher state
```

### parameters

Optional metadata about how the trainer produced transitions and GMMs. The block itself may be missing or empty;
all fields within it are optional.

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
     - Data type (e.g. {code}`float32`)
   * - encoding
     - String
     - Encoding format (e.g. {code}`nested_lists`)
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

If the drawn state has no GMM data ({code}`null`), the sampled normalized value is 0, which denormalizes to
{code}`min_power`. The trainer guarantees that states without GMM data are never reachable with non-zero
transition probability, so this case does not occur.

## Loading and Validation

Markov models are loaded via {code}`JsonMarkovProfileSource`, which:

- Reads the JSON file lazily on first access (not at construction time)
- Caches the parsed {code}`MarkovLoadModel` for subsequent calls
- Offers validation of all mandatory fields via {code}`validate()`. Note that this is a separate call:
  it is not triggered automatically when the model is loaded
- Is thread-safe ({code}`synchronized` lazy loading)

Markov models do not support energy scaling: {code}`getProfileEnergyScaling()` always returns
{code}`Optional.empty()`.

## Simulation Input and Output

Power values are requested via {code}`PowerValueSource.MarkovIdentifier`, which carries:

- {code}`time` - the timestamp of the requested step
- {code}`previousState` - the state returned by the previous step (used for all subsequent steps)
- {code}`initialNormalizedValue` - a normalized start value that is discretized into the initial state
  (used for the first step only)
- {code}`randomSeed` - the base seed for reproducible sampling

Either {code}`previousState` or {code}`initialNormalizedValue` has to be provided. Each step returns a
{code}`MarkovOutputValue` containing the sampled power value and the {code}`nextState`, which callers pass
into the identifier of the following step. The model itself is stateless, so a single instance can serve
multiple independent chains.

Sampling is deterministic: the RNG seed of a step is derived from the request seed, the time bucket, the
current state and the time slot, so identical inputs always produce identical results.
