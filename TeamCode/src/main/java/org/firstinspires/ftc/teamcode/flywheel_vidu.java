// ban bong chi dung motor
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Flywheel TEST")
public class flywheel_vidu extends OpMode {

    private DcMotor flywheel;
    private double flywheelPower = 0.0;

    private final double POWER_INCREMENT = 0.05;

    // Trạng thái nút
    private boolean startWasPressed = false;
    private boolean backWasPressed  = false;

    @Override
    public void init() {

        flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {

        boolean startIsPressed = gamepad1.start;
        boolean backIsPressed  = gamepad1.back;

        // TĂNG công suất bằng START
        if (startIsPressed && !startWasPressed) {
            flywheelPower += POWER_INCREMENT;
        }

        // GIẢM công suất bằng BACK
        if (backIsPressed && !backWasPressed) {
            flywheelPower -= POWER_INCREMENT;
        }

        startWasPressed = startIsPressed;
        backWasPressed  = backIsPressed;

        flywheelPower = Range.clip(flywheelPower, 0.0, 0.7);
        flywheel.setPower(flywheelPower);

        telemetry.addData("START", "Increase Power");
        telemetry.addData("BACK", "Decrease Power");
        telemetry.addData("Flywheel Power", "%.2f", flywheelPower);
        telemetry.update();
    }

    @Override
    public void stop() {
        flywheel.setPower(0.0);
    }
}



