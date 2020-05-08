.. _wec_result:

Wind Energy Converter
---------------------
Result of a wind turbine.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

+---------------+---------+--------------------------------------------------------------+
| Attribute     | Unit    | Remarks                                                      |
+===============+=========+==============================================================+
| uuid          | --      | uuid for the result entity                                   |
+---------------+---------+--------------------------------------------------------------+
| timeStamp     | --      | date and time for the produced result                        |
+---------------+---------+--------------------------------------------------------------+
| uuid          | --      | uuid for the associated input model                          |
+---------------+---------+--------------------------------------------------------------+
| p             | MW      |                                                              |
+---------------+---------+--------------------------------------------------------------+
| q             | MVAr    |                                                              |
+---------------+---------+--------------------------------------------------------------+

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!

.. _wec_cp_characteristic:

Betz Characteristic
^^^^^^^^^^^^^^^^^^^
A collection of wind velocity to Betz factor pairs to be applied in
`Betz's law <https://en.wikipedia.org/wiki/Betz's_law>`_ to determine the wind energy coming onto the rotor area.
