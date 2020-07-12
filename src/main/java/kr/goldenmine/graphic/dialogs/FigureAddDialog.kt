package kr.goldenmine.graphic.dialogs

import kr.theterroronline.util.physics.Vector3d
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.function.BiConsumer
import javax.swing.*

abstract class FigureAddDialog(
    private val ok: BiConsumer<List<Vector3d>, Color>
): JDialog() {
    private val okButton = JButton("OK")
    private val cancelButton = JButton("Cancel")

    // 자바에서 기본 지원하는 컬러 선택 유틸이다.
    private val colorChooser = JColorChooser(Color.BLACK)


    private val fields = ArrayList<JTextField>()

    abstract val keys: List<String>

    init {
        // 맨위를 Border로 만들어서 5조각으로 화면을 나누고
        layout = BorderLayout()

        // 윗부분에 놓을 패널을 설정할 것이다
        val panelTop = JPanel()
        // 뭐 이정도는 아시겠지만 FlowLayout은 배치하면 그 옆에 컴포넌트를 차곡차곡 쌓는 레이아웃이다.
        // (Border는 5조각으로 나누는 반면)
        panelTop.layout = FlowLayout()
        keys.forEach {
            val xLabel = JLabel("x of $it: ")
            val xField = JTextField("0.0")
            val yLabel = JLabel("y of $it: ")
            val yField = JTextField("0.0")
            val zLabel = JLabel("z of $it: ")
            val zField = JTextField("0.0")

            // 이걸 안하면 텍스트필드가 너무 작아서 글씨를 입력할 수 없으니 적당한 크기로 설정해준다
            xField.preferredSize = Dimension(40, 20)
            yField.preferredSize = Dimension(40, 20)
            zField.preferredSize = Dimension(40, 20)

            fields.add(xField)
            fields.add(yField)
            fields.add(zField)

            panelTop.add(xLabel)
            panelTop.add(xField)
            panelTop.add(yLabel)
            panelTop.add(yField)
            panelTop.add(zLabel)
            panelTop.add(zField)
        }

        // 아랫부분에 놓을 패널을 설정한다 확인과 취소버튼을 놓았다.
        val panelBottom = JPanel()
        panelBottom.add(okButton)
        panelBottom.add(cancelButton)

        // 이제 이 패널들을 적용시켜준다.
        add(panelTop, "North")
        add(colorChooser, "Center")
        add(panelBottom, "South")

        // ok 버튼을 눌렀을 때 다음과 같은 실행을 한다
        okButton.addActionListener { e: ActionEvent? ->
            // 콜백.
            // colorChooser.getColor()를 하면 컬러 선택창에서 설정한 색깔을 얻어올 수 있다.
            // TextField에서 값을 입력하고 불러온 값들은 기본적으로 String이기 때문에 Double이나 Integer로 바꿔줘야 숫자로서의 기능을 할 수 있게 된다.

            val result = ArrayList<Vector3d>()

            for(i in fields.indices step 3) {
                result.add(Vector3d(fields[i].text.toDouble(), fields[i + 1].text.toDouble(), fields[i + 2].text.toDouble(), true))
            }

            ok.accept(result, colorChooser.color)

            //창을 숨긴다
            cancel()
        }

        // cancel 버튼을 눌렀을 때
        cancelButton.addActionListener { e: ActionEvent? -> cancel() }

        // X버튼 또는 Alt + F4를 눌렀을때 창을 숨긴다
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                cancel()
            }

            override fun windowClosed(e: WindowEvent) {
                cancel()
            }
        })

        // 사이즈 설정
        setSize(500 + 40 * keys.size, 400)
        preferredSize = Dimension(500 + 40 * keys.size, 400)
    }


    fun setValues(points: List<Vector3d>, color: Color?) {
        for(i in points.indices) {
            val point = points[i]
            fields[3*i].text = point.x.toString()
            fields[3*i + 1].text = point.y.toString()
            fields[3*i + 2].text = point.z.toString()
        }
        colorChooser.color = color
    }

    // 이 창을 숨긴다
    fun cancel() {
        fields.forEach { it.text = "" }
        isVisible = false
    }
}