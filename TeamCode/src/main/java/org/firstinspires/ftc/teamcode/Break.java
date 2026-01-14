package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Mecanum_Brake")
public class Break extends LinearOpMode {

    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

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

        // 🔴 BẬT BRAKE CHO TẤT CẢ MOTOR
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while (opModeIsActive()) {

            double fwd    = -gamepad1.left_stick_y;
            double strafe =  gamepad1.left_stick_x;
            double rotate =  gamepad1.right_stick_x;

            double speed = gamepad1.right_bumper ? TURBO_SPEED : NORMAL_SPEED;

            // ✅ NẾU KHÔNG BẤM GÌ → DỪNG HẾT
            if (Math.abs(fwd) < 0.05 &&
                    Math.abs(strafe) < 0.05 &&
                    Math.abs(rotate) < 0.05) {

                stopAllMotors();
                telemetry.addData("Mode", "BRAKE");
            } else {
                drive(fwd, strafe, rotate, speed);
                telemetry.addData("Mode",
                        gamepad1.right_bumper ? "TURBO" : "NORMAL");
            }

            telemetry.update();
        }
    }

    private void stopAllMotors() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    public void drive(double fwd, double stf, double rotate, double speed) {

        double fl = fwd + stf + rotate;
        double fr = fwd - stf - rotate;
        double bl = fwd - stf + rotate;
        double br = fwd + stf - rotate;

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
