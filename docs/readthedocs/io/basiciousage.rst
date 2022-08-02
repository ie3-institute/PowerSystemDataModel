###
I/O
###
The PowerSystemDataModel library additionally offers I/O-capabilities.
In the long run, it is our aim to provide many different source and sink technologies.
Therefore, the I/O-package is structured as highly modular.

.. toctree::
   :maxdepth: 2

   influxdb
   csvfiles



Data sink structure
===================

.. plantuml:: DataSinkClassDiagram.puml
   :align: center
   :alt: Class diagram of data sink classes
   :width: 650


Data deployment
===============

.. plantuml:: InputDataDeployment.puml
   :align: center
   :alt: Diagram of input data deployment
   :width: 650
