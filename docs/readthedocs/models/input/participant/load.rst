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
| loadProfile         | --      | :ref:`Load profile<load_lp>` as model behaviour                                      |
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

.. _load_lp:

Load Profiles
^^^^^^^^^^^^^^^^^^^^^^
The :code:`LoadProfile` is an interface, that forces it's implementing classes to have a :code:`String` *key*
and being able to parse a :code:`String` to a :code:`LoadProfile`.
Its only purpose is to give note, which load profile has to be used by the simulation.
The actual profile has to be provided by the simulation itself.
If no matching standard load profile is known, :code:`LoadProfile#NO_LOAD_PROFILE` can be used.

To assist the user in marking the desired load profile, the enum :code:`BdewLoadProfile` provides a collection of
commonly known German standard electricity load profiles, defined by the bdew (Bundesverband der Energie- und
Wasserwirtschaft; engl. Federal Association of the Energy and Water Industry). For more details see
`the corresponding website (german only) <https://www.bdew.de/energie/standardlastprofile-strom/>`_.

Furthermore there are :code:`TemperatureDependantLoadProfiles` which can be used to note usage of load profiles for night heating storages or heat pumps for example.
The profiles rely on the VDN description for interruptable loads.
For more details see `here (german only) <https://www.bdew.de/media/documents/LPuVe-Praxisleitfaden.pdf/>`_.
:code:`NbwTemperatureDependantLoadProfiles` provides sample temperature dependant load profiles that can be used.
The `NbwTemperatureDependantLoadProfiles` consists of load profiles "ep1" for heat pumps and "ez2" for night storage heating.
