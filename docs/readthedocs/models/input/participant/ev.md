(ev-model)=

# Electric Vehicle

Model of an electric vehicle, that is occasionally connected to the grid via an {ref}`electric vehicle charging system<evcs_model>`.

## Attributes, Units and Remarks

### Type Model

| Attribute   | Unit     | Remarks                                                   |
| ----------- | -------- | --------------------------------------------------------- |
| uuid        | --       |                                                           |
| id          | --       | Human readable identifier                                 |
| capex       | €        | Capital expenditure to purchase one entity of this type   |
| opex        | € / MWh  | Operational expenditure to operate one entity ofthis type |
| eStorage    | kWh      | Available battery capacity                                |
| eCons       | kWh / km | Energy consumption per driven kilometre                   |
| sRated      | kVA      | Rated apparent power                                      |
| cosphiRated | --       | Rated power factor                                        |

### Entity Model

| Attribute     | Unit | Remarks                         |
| ------------- | ---- | ------------------------------- |
| uuid          | --   |                                 |
| id            | --   | Human readable identifier       |
| operator      | --   |                                 |
| operationTime | --   | Timely restriction of operation |
| node          | --   |                                 |
| type          | --   |                                 |

## Caveats

The {code}`node` attribute only marks the vehicles home connection point.
The actual connection to the grid is always given through {code}`EvcsInput`'s relation.
