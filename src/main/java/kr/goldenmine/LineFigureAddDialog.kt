package kr.goldenmine

import kr.theterroronline.util.physics.Vector3d
import java.awt.Color
import java.util.function.BiConsumer

class LineFigureAddDialog(
    ok: BiConsumer<List<Vector3d>, Color>
) : FigureAddDialog(ok) {
    override val keys: List<String>
        get() = listOf("position")
}