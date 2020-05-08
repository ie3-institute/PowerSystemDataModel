.. _cylindricalstorage_model:

Cylindrical Thermal Storage
--------------------------------
Model of a cylindrical thermal storage using a fluent to store thermal energy.

.. _cylindricalstorage_attributes:

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
+---------------------+----------------------------+----------------------------------------------+
| Attribute           | Unit                       | Remarks                                      |
+=====================+============================+==============================================+
| uuid                | --                         |                                              |
+---------------------+----------------------------+----------------------------------------------+
| id                  | --                         | Human readable identifier                    |
+---------------------+----------------------------+----------------------------------------------+
| operator            | --                         |                                              |
+---------------------+----------------------------+----------------------------------------------+
| operationTime       | --                         | Timely restriction of operation              |
+---------------------+----------------------------+----------------------------------------------+
| thermalBus          | --                         | Connection point to the thermal system       |
+---------------------+----------------------------+----------------------------------------------+
| storageVolumeLvl    | m³                         | Overall available storage volume             |
+---------------------+----------------------------+----------------------------------------------+
| storageVolumeLvlMin | m³                         | Minimum permissible storage volume           |
+---------------------+----------------------------+----------------------------------------------+
| inletTemp           | °C                         | Temperature of the inlet                     |
+---------------------+----------------------------+----------------------------------------------+
| returnTemp          | °C                         | Temperature of the outlet                    |
+---------------------+----------------------------+----------------------------------------------+
| c                   | kWh / (K :math:`\cdot` m³) | Specific heat capacity of the storage medium |
+---------------------+----------------------------+----------------------------------------------+

.. _cylindricalstorage_caveats:

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!
