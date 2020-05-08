.. _ev_model:

Electric Vehicle
-----------------------------
Model of an electric vehicle, that is occasionally connected to the grid via an :ref:`electric vehicle charging system<evcs_model>`.

.. _ev_attributes:

Attributes, Units and Hints
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. _ev_type_attributes:

Type Model
""""""""""
+-------------+----------+---------------------------------------------------------+
| Attribute   | Unit     | Hints                                                   |
+=============+==========+=========================================================+
| uuid        | --       |                                                         |
+-------------+----------+---------------------------------------------------------+
| id          | --       | Human readable identifier                               |
+-------------+----------+---------------------------------------------------------+
| capex       | €        | Capital expenditure to purchase one entity of this type |
+-------------+----------+---------------------------------------------------------+
| opex        | € / MWh  | | Operational expenditure to operate one entity of      |
|             |          | | this type                                             |
+-------------+----------+---------------------------------------------------------+
| eStorage    | kWh      | Available battery capacity                              |
+-------------+----------+---------------------------------------------------------+
| eCons       | kWh / km | Energy consumption per driven kilometre                 |
+-------------+----------+---------------------------------------------------------+
| sRated      | kVA      | Rated apparent power                                    |
+-------------+----------+---------------------------------------------------------+
| cosphiRated | --       | Rated power factor                                      |
+-------------+----------+---------------------------------------------------------+

.. _ev_entity_attributes:

Entity Model
""""""""""""

+------------------+---------+---------------------------------+
| Attribute        | Unit    | Hints                           |
+==================+=========+=================================+
| uuid             | --      |                                 |
+------------------+---------+---------------------------------+
| id               | --      | Human readable identifier       |
+------------------+---------+---------------------------------+
| operator         | --      |                                 |
+------------------+---------+---------------------------------+
| operationTime    | --      | Timely restriction of operation |
+------------------+---------+---------------------------------+
| node             | --      |                                 |
+------------------+---------+---------------------------------+
| type             | --      |                                 |
+------------------+---------+---------------------------------+

.. _ev_caveats:

Caveats
^^^^^^^
The :code:`node` attribute only marks the vehicles home connection point.
The actual connection to the grid is always given through :code:`EvcsInput`'s relation.
