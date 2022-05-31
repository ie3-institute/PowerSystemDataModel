.. _load_model:

Load
----
Model of (mainly) domestic loads.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

+---------------------+---------+--------------------------------------------------------------------------------------+
| Attribute           | Unit    | Remarks                                                                              |
+=====================+=========+======================================================================================+
| uuid                | --      |                                                                                      |
+---------------------+---------+--------------------------------------------------------------------------------------+
| id                  | --      | Human readable identifier                                                            |
+---------------------+---------+--------------------------------------------------------------------------------------+
| operator            | --      |                                                                                      |
+---------------------+---------+--------------------------------------------------------------------------------------+
| operationTime       | --      | Timely restriction of operation                                                      |
+---------------------+---------+--------------------------------------------------------------------------------------+
| node                | --      |                                                                                      |
+---------------------+---------+--------------------------------------------------------------------------------------+
| qCharacteristics    | --      | :ref:`Reactive power characteristic<participant_general_q_characteristic>` to follow |
+---------------------+---------+--------------------------------------------------------------------------------------+
| loadProfile | --      | :ref:`Standard load profile<load_slp>` as model behaviour                            |
+---------------------+---------+--------------------------------------------------------------------------------------+
| dsm                 | --      | Whether the load is able to follow demand side management signals                    |
+---------------------+---------+--------------------------------------------------------------------------------------+
| eConsAnnual         | kWh     | Annual energy consumption                                                            |
+---------------------+---------+--------------------------------------------------------------------------------------+
| sRated              | kVA     | Rated apparent power                                                                 |
+---------------------+---------+--------------------------------------------------------------------------------------+
| cosphiRated         | --      | Rated power factor                                                                   |
+---------------------+---------+--------------------------------------------------------------------------------------+

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!

.. _load_slp:

Standard Load Profiles
^^^^^^^^^^^^^^^^^^^^^^
The :code:`StandardLoadProfile` is an interface, that forces it's implementing classes to have a :code:`String` *key*
and being able to parse a :code:`String` to an :code:`StandardLoadProfile`.
Its only purpose is to give note, which standard load profile has to be used by the simulation.
The actual profile has to be provided by the simulation itself.
If no matching standard load profile is known, :code:`StandardLoadProfile#NO_STANDARD_LOAD_PROFILE` can be used.

To assist the user in marking the desired load profile, the enum :code:`BdewLoadProfile` provides a collection of
commonly known German standard electricity load profiles, defined by the bdew (Bundesverband der Energie- und
Wasserwirtschaft; engl. Federal Association of the Energy and Water Industry). For more details see
`the corresponding website (German only) <https://www.bdew.de/energie/standardlastprofile-strom/>`_.
