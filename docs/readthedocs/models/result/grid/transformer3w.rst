.. _transformer3W_result:

Three Winding Transformer
-------------------------
Representation of three winding transformers.

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. list-table::
   :widths: 33 33 33
   :header-rows: 0


   * - Attribute
     - Unit
     - Remarks

   * - uuid
     - --
     - uuid for the result entity

   * - time
     - ZonedDateTime
     - date and time for the produced result

   * - inputModel
     - --
     - uuid for the associated input model

   * - iAMag
     - ampere
     - A stands for sending node

   * - iAAng
     - degree
     - 

   * - iBMag
     - ampere
     - B stands for receiving node

   * - iBAng
     - degree
     - 

   * - iCMag
     - ampere
     - B stands for receiving node

   * - iCAng
     - degree
     - 

   * - tapPos
     - --
     - 


Caveats
^^^^^^^
Assumption: Node A is the node at highest voltage and Node B is at intermediate voltage.
For model specifications please check corresponding input model documentation.
