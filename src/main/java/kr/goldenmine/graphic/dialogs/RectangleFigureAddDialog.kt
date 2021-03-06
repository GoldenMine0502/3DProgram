package kr.goldenmine.graphic.dialogs

import kr.theterroronline.util.physics.Vector3d
import java.awt.Color
import java.util.function.BiConsumer

class RectangleFigureAddDialog(
    ok: BiConsumer<List<Vector3d>, Color>
) : FigureAddDialog(ok) {
    override val keys: List<String>
        get() = listOf("position", "size")
}