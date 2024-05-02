(operator-model)=

# Operator

This is a simple identifier object, representing a natural or legal person that is the owner or responsible person
having control over one or more physical entitites.

## Attributes, Units and Remarks

```{eval-rst}
.. list-table::
   :widths: 33 33 33
   :header-rows: 1


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     -

   * - id
     - --
     - Human readable identifier

```

(operator-example)=

## Application example

```{code-block} java
:linenos: true

OperatorInput profBroccoli = new OperatorInput(
        UUID.fromString("f15105c4-a2de-4ab8-a621-4bc98e372d92"),
        "Univ.-Prof. Dr. rer. hort. Klaus-Dieter Brokkoli"
      )
```

## Caveats

Nothing - at least not known.
If you found something, please contact us!
