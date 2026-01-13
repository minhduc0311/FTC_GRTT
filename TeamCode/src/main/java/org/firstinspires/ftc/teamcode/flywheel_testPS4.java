package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;


@TeleOp(name="Flywheel TEST PS4")
public class flywheel_testPS4 extends OpMode {
    private DcMotor flywheel;
    private double flywheelPower = 0.0;

    private final double POWER_INCREMENT = 0.05;

    private boolean dpadUpWasPressed = false;
    private boolean dpadDownWasPressed = false;

    @Override
    public void init() {
        this.flywheel = hardwareMap.get(DcMotor.class, "flywheel");

        this.flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.flywheel.setDirection(DcMotor.Direction.REVERSE);

        this.telemetry.addData("Status", "Initialized");
        this.telemetry.addData(">", "Press Start to run");
        this.telemetry.update();
    }

    @Override
    public void start() {
        this.flywheel.setPower(flywheelPower);

        this.telemetry.addData("Status", "Running");
        this.telemetry.update();
    }

    @Override
    public void loop() {

        boolean dpadUpIsPressed = gamepad1.dpad_up;
        boolean dpadDownIsPressed = gamepad1.dpad_down;


        // TANG GIAM CONG SUAT
        if (dpadUpIsPressed && !dpadUpWasPressed) {
            this.flywheelPower += POWER_INCREMENT;
        } else if (dpadDownIsPressed && !dpadDownWasPressed) {
            this.flywheelPower -= POWER_INCREMENT;
        }

        dpadUpWasPressed = dpadUpIsPressed;
        dpadDownWasPressed = dpadDownIsPressed;

        flywheelPower = Range.clip(flywheelPower, 0.0, 1.0);

        this.flywheel.setPower(flywheelPower);


        this.telemetry.addData("--- Controls ---", "");
        this.telemetry.addData("Flywheel", "D-Pad Up/Down");
        this.telemetry.addData("--- Status ---", "");
        this.telemetry.addData("Flywheel Target Power", "%.2f", flywheelPower);
        this.telemetry.update();
    }

    @Override
    public void stop() {
        this.flywheel.setPower(0.0);

        this.telemetry.addData("Status", "Stopped");
        this.telemetry.update();
    }
}
