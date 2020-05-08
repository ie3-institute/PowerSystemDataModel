.. _measurement_unit_model:

Measurement Unit
----------------
Representation of a measurement unit placed at a node.
It can be used to mark restrictive access to simulation results to e.g. control algorithms.
The measured information are indicated by boolean fields.

.. _measurement_unit_attributes:

Attributes, Units and Hints
^^^^^^^^^^^^^^^^^^^^^^^^^^^
+---------------+------+----------------------------------------------+
| Attribute     | Unit | Hints                                        |
+===============+======+==============================================+
| uuid          | --   |                                              |
+---------------+------+----------------------------------------------+
| id            | --   | Human readable identifier                    |
+---------------+------+----------------------------------------------+
| operator      | --   |                                              |
+---------------+------+----------------------------------------------+
| operationTime | --   | Timely restriction of operation              |
+---------------+------+----------------------------------------------+
| node          | --   |                                              |
+---------------+------+----------------------------------------------+
| vMag          | --   | Voltage magnitude measurements are available |
+---------------+------+----------------------------------------------+
| vAng          | --   | Voltage angle measurements are available     |
+---------------+------+----------------------------------------------+
| p             | --   | Active power measurements are available      |
+---------------+------+----------------------------------------------+
| q             | --   | Reactive power measurements are available    |
+---------------+------+----------------------------------------------+

.. _measurement_caveats:

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
