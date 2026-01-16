package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
@TeleOp(name="Motor")
public class Motor_Basic {
    private DcMotor motor;
    private Telemetry telemetry;
    private double power = 1; //tuy chinh

    public void init(HardwareMap hwmap, Telemetry telemetry) {
        this.motor = hardwareMap.get(DcMotor.class, "flywheel");//remember to change motor's name
        this.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.motor.setDirection(DcMotor.Direction.REVERSE);
        this.telemetry = telemetry;

        this.motor.setPower(power);
    }

    public void setPower(boolean increase, boolean decrease) {
        this.motor.setPower(power);
    }
    public void setPower(double power) {
        this.power = power;
        this.power = Range.clip(this.power, 0.0, 1.0);
        this.motor.setPower(this.power);
    }
    public double getPower() {
        return this.power;
    }
    public void stop() {
        this.motor.setPower(0.0);
    }
    public void periodic() {
        if(telemetry != null) {
            telemetry.addData("Motor Target Power", "%.2f", getPower());
            telemetry.addData("Motor Actual Power", "%.2f", this.motor.getPower());
        }
    }
}

