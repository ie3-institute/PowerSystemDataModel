# Validation Utils
This page gives an overview about the ValidationUtils in the *PowerSystemDataModel*.

## What are the ValidationUtils?
The methods in ValidationUtils and subclasses can be used to check that objects are valid, meaning their parameters have valid values and they are correctly connected.

## What is checked?
- The check methods include checks that assigned values are valid, e.g. lines are not allowed to have negative lengths or the rated power factor of any unit must be between 0 and 1.
- Furthermore, several connections are checked, e.g. that lines only connect nodes of the same voltage level or that the voltage levels indicated for the transformer sides match the voltage levels of the nodes they are connected to.

## How does it work?
- The method `ValidationUtils.check(Object)` is the only method that should be called by the user.
- This check method identifies the object class and forwards it to a specific check method for the given object
- The overall structure of the ValidationUtils methods follows a cascading scheme, orientated along the class tree
- Example: A `LineInput lineInput` should be checked
    1. `ValidationUtils.check(lineInput)` is called
    2. `ValidationUtils.check(lineInput)` identifies the class of the object as `AssetInput` and calls `ValidationUtils.checkAsset(lineInput)`
    3. `ValidationUtils.checkAsset(lineInput)`, if applicable, checks those parameters that all `AssetInput` have in common (e.g. operation time) and further identifies the object, more specifically, as a `ConnectorInput` and calls `ConnectorValidationUtils.check(lineInput)`
    4. `ConnectorValidationUtils.check(lineInput)`, if applicable, checks those parameters that all `ConnectorInput` have in common and further identifies the object, more specifically, as a `LineInput` and calls `ConnectorValidationUtils.checkLine(lineInput)`
    5. `ConnectorValidationUtils.checkLine(lineInput)` checks all specific parameters of a `LineInput`
- ValidationUtils furthermore contains several utils methods used in the subclasses

## Which objects are checked?
The ValidationUtils include validation checks for...

- NodeValidationUtils
    - NodeInput
    - VoltageLevel
- ConnectorValidationUtils:
    - ConnectorInput
        - LineInput
        - Transformer2WInput
        - Transformer3WInput
        - SwitchInput
    - ConnectorTypeInput
        - LineTypeInput
        - Transformer2WTypeInput
        - Transformer3WTypeInput
- MeasurementUnitValidationUtils
    - MeasurementUnitInput
- SystemParticipantValidationUtils
    - SystemParticipantInput
        - BmInput
        - ChpInput
        - EvInput
        - FixedFeedInInput
        - HpInput
        - LoadInput
        - PvInput
        - StorageInput
        - WecInput
        - (missing: EvcsInput)
    - SystemParticipantTypeInput
        - BmTypeInput
        - ChpTypeInput
        - EvTypeInput
        - HpTypeInput
        - StorageTypeInput
        - WecTypeInput
        - (missing: EvcsTypeInput/ChargingPointType)
- ThermalUnitValidationUtils
    - ThermalUnitInput
        - ThermalSinkInput
            - ThermalHouseInput
        - ThermalStorageInput
            - CylindricalStorageInput
- GraphicValidationUtils
    - GraphicInput
        - LineGraphicInput
        - NodeGraphicInput
- GridContainerValidationUtils
    - GraphicElements
    - GridContainer
    - RawGridElements
    - SystemParticipants

## What should be considered?
- Due to many checks with if-conditions, the usage of the ValidationUtils for many objects might be runtime relevant.
- The check for a GridContainer includes the interplay of the contained entities as well as the checks of all contained entities.
- If new classes are introduced to the *PowerSystemDataModel*, make sure to follow the forwarding structure of the ValidationUtils methods when writing the check methods!
