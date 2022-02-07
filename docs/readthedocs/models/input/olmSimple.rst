.. _olm_model:

overhead line monitoring
----
Representation of a simple olm type.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Type Model
""""""""""

+-------------------+---------+--------------------------------------------------------+
| Attribute         | Unit    | Remarks                                                |
+===================+=========+========================================================+
| uuid              | --      |                                                        |
+-------------------+---------+--------------------------------------------------------+
| id                | --      | Human readable identifier                              |
+-------------------+---------+--------------------------------------------------------+
| olmCharacteristic | --      | | Characteristic of possible overhead line monitoring  |
|                   |         | | Can be given in the form of `olm:{<List of Pairs>}`. |
|                   |         | | The pairs are wind velocity in x and permissible     |
|                   |         | | loading in y.                                        |
+-------------------+---------+--------------------------------------------------------+




Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!