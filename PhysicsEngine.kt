import org.joml.Vector3f
import kotlin.math.*

class PhysicsEngine {
    private val verletIntegrator = VerletIntegrator()
    private val collisionDetector = CollisionDetector()
    
    fun calculateForces(atom: Atom, atoms: List<Atom>, molecules: List<Molecule>): Vector3f {
        val forces = Vector3f()
        
        // Gravitational forces
        atoms.forEach { other ->
            if (other != atom) {
                forces += calculateGravitationalForce(atom, other)
            }
        }
        
        // Electromagnetic forces
        atoms.forEach { other ->
            if (other != atom) {
                forces += calculateCoulombForce(atom, other)
            }
        }
        
        // Molecular bonds
        molecules.forEach { molecule ->
            if (molecule.atoms.contains(atom)) {
                forces += calculateBondForces(atom, molecule)
            }
        }
        
        return forces
    }
    
    private fun calculateBondForces(atom: Atom, molecule: Molecule): Vector3f {
        val bondForce = Vector3f()
        
        molecule.bonds.forEach { bond ->
            if (bond.atom1 == atom || bond.atom2 == atom) {
                val otherAtom = if (bond.atom1 == atom) bond.atom2 else bond.atom1
                val direction = otherAtom.position - atom.position
                val distance = direction.length()
                val strength = bond.strength * (distance - bond.type.equilibriumDistance)
                bondForce += direction.normalize() * strength
            }
        }
        
        return bondForce
    }
} 