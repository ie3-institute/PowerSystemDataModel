# Code generation

With version 9.2 the data model is defined by a YAML file (`src/main/resources/datamodel.yaml`) that is included in the
resources. This file describes the available model classes with their fields and inheritance structure.

With the help of a generation file (`src/codegen/resources/generation.yaml`) the models classes are generated. Both YAML
files contain a mapping between a package name and a list of definitions.


## Examples

An example for a definition is shown below:

````yaml
    AssetInput:
      name: AssetInput
      description: "Describes a grid asset under the assumption that every asset could be operable."
      extends: UniqueInputEntity
      components:
        - name: id
          type: String
          description: "Name or ID of the asset."
        - name: operator
          type: OperatorInput
          required: false
          nested: true
          description: "The operator of this asset."
        - name: operationTime
          type: OperationTime
          keys: [operatesFrom, operatesUntil]
          required: false
          description: "Time for which the entity is operated."
````

An example for a generation definition is shown below:

````yaml
    AssetInput:
      inherits: [Operable]
      keyMapper:
        operatesFrom: operationTime.getStartDate().map(TimeUtil.withDefaults::toString).orElse("")
        operatesUntil: operationTime.getEndDate().map(TimeUtil.withDefaults::toString).orElse("")
````


## Model definitions

To define a model the following structure is used:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks

   * - name
     - String
     - Name of the model.

   * - description
     - String
     - Description of the model class.
     
   * - abstract
     - boolean
     - If {@code true} the class is abstract (default: true).
     
   * - extends
     - String
     - Defines a superior model class that is extended by this class.

   * - components
     - List
     - List of components (fields) of this model.

   * - nested
     - List
     - List with nested model definitions.

```


## Component definitions

The components are defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks

   * - name
     - String
     - Name of the component.

   * - type
     - String
     - Type of the component.

   * - description
     - String
     - Description of the component.
     
   * - keys
     - List
     - List with keys that are used in the source.
     
   * - required
     - boolean
     - If {@code true} the field is required to build this model (default: true).

   * - nested
     - boolean
     - If {@code true} the component is a nested model (default: false).

   * - nullable
     - boolean
     - If {@code true} the source can provide null values (default: false).
     
````


## Parameter definitions

A parameter is defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks

   * - name
     - String
     - Name of the component.

   * - type
     - String
     - Type of the component.

   * - description
     - String
     - Description of the component.

````


## Parameter definitions

A parameter is defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks

   * - name
     - String
     - Name of the component.

   * - type
     - String
     - Type of the component.

   * - description
     - String
     - Description of the component.

````


## Generation configuration

A generation configuration is defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks

   * - modifiers
     - List
     - List of all class modifiers (default: only public).

   * - inherits
     - List
     - List of inherited interfaces.

   * - getters
     - boolean
     - If getters should be generated.

   * - setter
     - boolean
     - If setters should be generated.

   * - equals
     - boolean
     - If an equals method should be generated.

   * - hashCode
     - boolean
     - If a hashCode method should be generated.

   * - toString
     - boolean
     - If a toString method should be generated.
     
   * - toMap
     - boolean
     - If a toMap method should be generated.
     
   * - copy
     - boolean
     - If a copy method and a copy builder should be generated.
     
   * - constructors
     - List
     - List with additional constructor definitions.

   * - fields
     - List
     - List with additional field definitions.

   * - methods
     - List
     - List with additional method definitions.

   * - copyBuilderMethods
     - List
     - List with additional method definitions in a copy builder.

   * - keyMapper
     - Map
     - Map: key to expression. This is used when providing keys for a field.

   * - excludeFromMethods
     - List
     - List with fields that should be excluded from all methods. This applies to setters, toMap, equals, hashCode and toString.

   * - nonCapitalized
     - List
     - List of method that should not start capitalized after `get`.

````


## Constructor definition

A constructor generation configuration is defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks
     
   * - private
     - boolean
     - If {@code true} the constructor is private.

   * - parameters
     - List
     - List of constructor parameters.

   * - codeBlock
     - String
     - String that contains a block of code that is inserted in the constructor.

````


## Field definition

A field generation configuration is defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks
     
   * - name
     - String
     - Name of the field.

   * - type
     - String
     - Type of the field.

   * - description
     - String
     - Description (Javadoc) of the field or method. For multi line statements `\n` needs to be present in the string.

   * - expression
     - String
     - The expression. It can be multiple lines, if `\n` is used.

   * - modifiers
     - List
     - List of all modifiers.

````


## Method definition

A method generation configuration is defined with:

```{list-table}
   :widths: auto
   :header-rows: 1
   :class: wrapping

   * - Attribute
     - Type
     - Remarks
     
   * - name
     - String
     - Name of the method.

   * - type
     - String
     - Return type of the method.

   * - description
     - String
     - Description (Javadoc) of the field or method. For multi line statements `\n` needs to be present in the string.

   * - expression
     - String
     - The expression. It can be multiple lines, if `\n` is used.

   * - modifiers
     - List
     - List of all modifiers.

   * - addReturn
     - boolean
     - If {@code true} a return statement is inserted before the {@link #expression}.

   * - annotation
     - boolean
     - If {@code true} a {@link Override} annotation is added.

   * - parameters
     - List
     - List of method parameters.

````
