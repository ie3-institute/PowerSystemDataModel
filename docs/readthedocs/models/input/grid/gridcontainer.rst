.. _grid_container_model:

Grid Container
--------------
The grid container groups all entities that are able to form a full grid model.
Two types of grid containers are available:

JointGridContainer
   This one is able to hold a grid model spanning several voltage levels.
   On instantiation, a sub grid topology graph is built.
   This graph holds :code:`SubGridContainers` as vertices and transformer models as edges.
   Thereby, you are able to discover the topology of galvanically separated sub grids and access those sub models
   directly.

and

SubGridContainer
   This one is meant to hold all models, that form a galvanically separated sub grid.
   In contrast to the :code:`JointGridContainer` it only covers one voltage level and therefore has an additional field
   for the predominant voltage level apparent in the container.
   Why predominant?
   As of convention, the :code:`SubGridContainers` hold also reference to the transformers leading to higher sub grids
   and their higher voltage coupling point.

   .. figure:: ../../../_static/figures/transformerWithSwitchGear.png
      :align: center
      :alt: Sub grid boundary definition for transformers with upstream switchgear

   Let's shed a more detailed light on the boundaries of a sub grid as of our definition.
   This especially is important, if the switchgear of the transformer is modeled in detail.
   We defined, that all nodes in upstream direction of the transformer, that are connected by switches *only* (therefore
   are within the switchgear) are counted towards the inferior sub grid structure (here "2"), although they belong to a
   different voltage level.
   This decision is taken, because we assume, that the interest to operate on the given switchgear will most likely be
   placed in the inferior grid structure.

   The "real" coupling node A is not comprised in the sub grids node collection, but obviously has reference through the
   switch between nodes A and B.

A synoptic overview of both classes' attributes is given here:

Attributes, Units and Remarks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
+-------------------------+------+---------------------------------------------------------+
| Attribute               | Unit | Remarks                                                 |
+=========================+======+=========================================================+
| gridName                | --   | Human readable identifier                               |
+-------------------------+------+---------------------------------------------------------+
| rawGrid                 | --   | see below                                               |
+-------------------------+------+---------------------------------------------------------+
| systemParticipants      | --   | see below                                               |
+-------------------------+------+---------------------------------------------------------+
| graphics                | --   | see below                                               |
+-------------------------+------+---------------------------------------------------------+
| subGridTopologyGraph    | --   | topology of sub grids - only :code:`JointGridContainer` |
+-------------------------+------+---------------------------------------------------------+
| predominantVoltageLevel | --   | main voltage level - only :code:`SubGridContainer`      |
+-------------------------+------+---------------------------------------------------------+
| subnet                  | --   | sub grid number - only :code:`SubGridContainer`         |
+-------------------------+------+---------------------------------------------------------+

.. _grid_container_raw_grid_elements:

RawGridElements
"""""""""""""""
This sub container simply holds:

   * :ref:`nodes<node_model>`
   * :ref:`lines<line_model>`
   * :ref:`switches<switch_model>`
   * :ref:`two winding transformers<transformer2w_model>`
   * :ref:`three winding transformers<transformer3w_model>`
   * :ref:`measurement units<measurement_unit_model>`

.. _grid_container_system_participants:

SystemParticipants
""""""""""""""""""
This sub container simply holds:

   * :ref:`biomass plants<bm_model>`
   * :ref:`combined heat and power plants<chp_model>`
   * :ref:`electric vehicles<ev_model>`
   * :ref:`electric vehicle charging stations<evcs_model>`
   * :ref:`fixed feed in facilities<fixed_feed_in_model>`
   * :ref:`heat pumps<hp_model>`
   * :ref:`loads<load_model>`
   * :ref:`photovoltaic power plants<pv_model>`
   * :ref:`electrical energy storages<storage_model>`
   * :ref:`wind energy converters<wec_model>`

and the needed nested thermal models.

.. _grid_container_graphics:

Graphics
""""""""
This sub container simply holds:

   * :ref:`schematic node graphics<node_graphic_model>`
   * :ref:`schematic line graphics<line_graphic_model>`

Container Concept
"""""""""""""""""
   .. figure:: ../../../_static/figures/uml/ModelContainerConcept.png
      :align: center
      :alt: Model container concept

Caveats
^^^^^^^
Nothing - at least not known.
If you found something, please contact us!