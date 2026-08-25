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
    - textual description of the cable layout (e.g., "TREFOIL")
     
  * - depthCables
    - Metre
    - depth of the cable
     
  * - distanceCables
    - Metre
    - distance between cables 
     
  * - additionalInformation
    - –
    - optional map with any additional, string-encoded information
```


### Remarks

* Cable deployments are read from input sources (CSV, database) using a dedicated factory `CableDeploymentInputFactory` that parses input rows into `CableDeploymentInput` instances.
* The ingestion process validates references: if a cable deployment references a non-existing line UUID during assembly of the `RawGridElements` container, a `SourceException` is thrown to maintain fail-fast semantics.

