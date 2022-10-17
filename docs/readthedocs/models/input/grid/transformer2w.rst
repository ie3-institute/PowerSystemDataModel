.. _transformer2w_model:

Two Winding Transformer
-----------------------
Model of a two winding transformer.
It is assumed, that node A is the node with higher voltage.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Type Model
""""""""""
All impedances and admittances are given with respect to the higher voltage side.
As obvious, the parameter can be used in T- as in 𝜋-equivalent circuit representations.

+-----------+------+---------------------------------------------------------+
| Attribute | Unit | Remarks                                                 |
+===========+======+=========================================================+
| uuid      |      |                                                         |
+-----------+------+---------------------------------------------------------+
| id        |      | Human readable identifier                               |
+-----------+------+---------------------------------------------------------+
| rSc       | Ω    | Short circuit resistance                                |
+-----------+------+---------------------------------------------------------+
| xSc       | Ω    | Short circuit reactance                                 |
+-----------+------+---------------------------------------------------------+
| gM        | nS   | No load conductance                                     |
+-----------+------+---------------------------------------------------------+
| bM        | nS   | No load susceptance                                     |
+-----------+------+---------------------------------------------------------+
| sRated    | kVA  | Rated apparent power                                    |
+-----------+------+---------------------------------------------------------+
| vRatedA   | kV   | Rated voltage at higher voltage terminal                |
+-----------+------+---------------------------------------------------------+
| vRatedB   | kV   | Rated voltage at lower voltage terminal                 |
+-----------+------+---------------------------------------------------------+
| dV        | %    | Voltage magnitude increase per tap position             |
+-----------+------+---------------------------------------------------------+
| dPhi      | °    | Voltage angle increase per tap position                 |
+-----------+------+---------------------------------------------------------+
| tapSide   |      | true, if tap changer is installed on lower voltage side |
+-----------+------+---------------------------------------------------------+
| tapNeutr  |      | Neutral tap position                                    |
+-----------+------+---------------------------------------------------------+
| tapMin    |      | Minimum tap position                                    |
+-----------+------+---------------------------------------------------------+
| tapMax    |      | Maximum tap position                                    |
+-----------+------+---------------------------------------------------------+

A list with some standard transformer types can be found here: :doc:`standardgridassettypes`


Entity Model
""""""""""""

+-----------------+------+------------------------------------------------------------+
| Attribute       | Unit | Remarks                                                    |
+=================+======+============================================================+
| uuid            | --   |                                                            |
+-----------------+------+------------------------------------------------------------+
| id              | --   | Human readable identifier                                  |
+-----------------+------+------------------------------------------------------------+
| operator        | --   |                                                            |
+-----------------+------+------------------------------------------------------------+
| operationTime   | --   | Timely restriction of operation                            |
+-----------------+------+------------------------------------------------------------+
| nodeA           | --   | Higher voltage node                                        |
+-----------------+------+------------------------------------------------------------+
| nodeB           | --   | Lower voltage node                                         |
+-----------------+------+------------------------------------------------------------+
| parallelDevices | --   | | overall amount of parallel transformers to automatically |
|                 |      | | construct (e.g. parallelDevices = 2 will build a         |
|                 |      | | total of two transformers using the specified parameters)|
+-----------------+------+------------------------------------------------------------+
| type            | --   |                                                            |
+-----------------+------+------------------------------------------------------------+
| tapPos          | --   | Current position of the tap changer                        |
+-----------------+------+------------------------------------------------------------+
| autoTap         | --   | true, if there is a tap regulation apparent and active     |
+-----------------+------+------------------------------------------------------------+

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
