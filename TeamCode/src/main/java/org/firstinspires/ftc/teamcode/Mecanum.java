// khai bao dc
// trai tren 0  trai duoi 1  phai tren 2  phai duoi 3

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Mecanum")
public class Mecanum extends LinearOpMode {

    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

  // toc do motor
    private static final double NORMAL_SPEED = 0.7;
    private static final double TURBO_SPEED  = 1.0;

    @Override
    public void runOpMode() {


        frontLeftMotor  = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        backLeftMotor   = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor  = hardwareMap.get(DcMotor.class, "backRightMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");


        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double fwd    = -gamepad1.left_stick_y;   // forward/back
            double strafe =  gamepad1.left_stick_x;   // left/right
            double rotate =  gamepad1.right_stick_x;  // turn
      // rb de chay nhanh
            double speedMultiplier = gamepad1.right_bumper ? TURBO_SPEED : NORMAL_SPEED;

            drive(fwd, strafe, rotate, speedMultiplier);

            telemetry.addData("Speed Mode",
                    gamepad1.right_bumper ? "TURBO (100%)" : "NORMAL (70%)");
            telemetry.update();
        }
    }

    public void drive(double fwd, double stf, double rotate, double speed) {

        double fl = fwd + stf + rotate;
        double fr = fwd - stf - rotate;
        double bl = fwd - stf + rotate;
        double br = fwd + stf - rotate;
// che do nomal
        double max = Math.max(1.0,
                Math.max(Math.abs(fl),
                        Math.max(Math.abs(fr),
                                Math.max(Math.abs(bl), Math.abs(br)))));

        frontLeftMotor.setPower((fl / max) * speed);
        frontRightMotor.setPower((fr / max) * speed);
        backLeftMotor.setPower((bl / max) * speed);
        backRightMotor.setPower((br / max) * speed);
    }
}
