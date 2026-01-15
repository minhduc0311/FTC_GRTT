package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="Test 2 Servo")
public class ServoTest extends OpMode {

    private Servo lamgay;
    private Servo ducgay;

    // Trạng thái nút Y
    private boolean yWasPressed = false;

    // Trạng thái servo
    private boolean servoAt90 = false;

    // Vị trí servo
    private final double SERVO_0_DEG = 0.0;
    private final double SERVO_90_DEG = 0.67;

    @Override
    public void init() {

        lamgay = hardwareMap.get(Servo.class, "lamgay");
        ducgay = hardwareMap.get(Servo.class, "ducgay");

        // Vị trí ban đầu
        lamgay.setPosition(SERVO_0_DEG);
        ducgay.setPosition(SERVO_0_DEG);

        telemetry.addData("Status", "2 Servo Ready");
        telemetry.update();
    }

    @Override
    public void loop() {

        boolean yIsPressed = gamepad1.y;

        // Toggle khi nút Y vừa được nhấn
        if (yIsPressed && !yWasPressed) {
            servoAt90 = !servoAt90;

            if (servoAt90) {
                lamgay.setPosition(SERVO_90_DEG);
                ducgay.setPosition(SERVO_90_DEG);
            } else {
                lamgay.setPosition(SERVO_0_DEG);
                ducgay.setPosition(SERVO_0_DEG);
            }
        }

        yWasPressed = yIsPressed;

        telemetry.addData("lamgay", servoAt90 ? "90 DEG" : "0 DEG");
        telemetry.addData("ducgay", servoAt90 ? "90 DEG" : "0 DEG");
        telemetry.update();
    }
}
