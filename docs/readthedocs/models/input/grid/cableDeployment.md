# Cable Deployment Input

Describes the installation environment and deployment parameters of a cable that is associated with a line.

This model is represented by the Java class `edu.ie3.datamodel.models.input.connector.CableDeploymentInput` and is persisted as an independent unique input entity (has its own UUID). Cable deployments are linked to a line via the `lineUuid` attribute and are collected in the `RawGridElements` container, grouped by the referenced line UUID. Access the mapping via `RawGridElements#getCableDeploymentsByLine()` which returns an unmodifiable map of lists (each list is unmodifiable as well).

## Attributes, Units and Remarks

### Entity Model

```{list-table}
   :widths: auto
   :class: wrapping
   :header-rows: 1

  * - Attribute
    - Unit
    - Remarks
   
  * - uuid
    - –
    - unique identifier for the cable deployment input
    
  * - lineUuid
    - –
    - UUID of the line this cable deployment refers to
    
  * - layoutFormation
    - –
    - textual description of the cable layout (e.g., "trefoil-touching")
   
  * - depthCables
    - Metre
    - depth of the cable
   
  * - distanceCables
    - Metre
    - distance between cables 
   
  * - cableBedWidth
    - Metre
    - width of the rectangular cable bed (optional, see [Cable Bed](#cable-bed))
   
  * - cableBedHeight
    - Metre
    - height of the rectangular cable bed (optional, see [Cable Bed](#cable-bed))
   
  * - cableBedSoilType
    - –
    - UUID of the [soil type](/models/input/grid/soilData) used for the cable bed (optional, see [Cable Bed](#cable-bed))
```

### Cable Bed

The three attributes `cableBedWidth`, `cableBedHeight`, and `cableBedSoilType` describe the
rectangular cable bed that a cable is laid in. They form an **optional group**:

* If none of the three is set, the cable is assumed to be laid without an explicit cable bed and
  all three remain `null`.
* If any of the three is set, all three must be set: `cableBedWidth` and `cableBedHeight` must be
  finite and positive lengths, and `cableBedSoilType` must reference a soil type.

The `cableBedSoilType` UUID references a `SoilType` available in the owning
[grid container](/models/input/grid/gridcontainer) (see [soil data](/models/input/grid/soilData)).
This reference allows the thermal model to determine the properties of the soil that directly
surrounds the cable bed.

### Remarks

* Cable deployments are read from input sources (CSV, database) using a dedicated factory `CableDeploymentInputFactory` that parses input rows into `CableDeploymentInput` instances.
* The ingestion process validates references: if a cable deployment references a non-existing line UUID during assembly of the `RawGridElements` container, a `SourceException` is thrown to maintain fail-fast semantics.

## Validation

The validation is split by scope:

* `ConnectorValidationUtils#checkCableDeployment` validates the cable deployment as a single entity. Besides the layout formation, depth, and cable distance checks, it validates the cable bed as an optional group: either all of `cableBedWidth`, `cableBedHeight`, and `cableBedSoilType` are absent, or, when any of them is present, `cableBedWidth` and `cableBedHeight` must be finite and positive and `cableBedSoilType` must be provided.
* `GridContainerValidationUtils` validates the cross-reference: if a cable deployment supplies a `cableBedSoilType`, the referenced UUID must denote a soil type that is available in the grid container. Otherwise the grid is considered invalid.