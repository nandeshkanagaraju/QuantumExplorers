package fm.mrc.quantumexplorers.model

data class ChemistryExperiment(
    val title: String,
    val description: String,
    val difficulty: Int, // 1-5
    val visualizationType: VisualizationType,
    val interactiveElements: List<InteractiveElement>,
    val safetyTips: List<String>
)

enum class VisualizationType {
    PARTICLE_COLLISION,
    MOLECULAR_FORMATION,
    STATE_CHANGE,
    ELECTRON_TRANSFER,
    BOND_FORMATION
}

data class InteractiveElement(
    val name: String,
    val description: String,
    val controlType: ControlType
)

enum class ControlType {
    SLIDER,
    BUTTON,
    DRAG_DROP,
    ROTATE_3D,
    MIXER
} 