package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Mecanum_RPM")
public class Mecanum_RPM extends LinearOpMode {

    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    // Speed mode
    private static final double NORMAL_SPEED = 0.7;
    private static final double TURBO_SPEED  = 1.0;

    // ===== ENCODER =====
    // GoBILDA 312RPM / 435RPM thường là 537.6
    private static final double TICKS_PER_REV = 537.6;

    private int flLast = 0, frLast = 0, blLast = 0, brLast = 0;

    private ElapsedTime flTimer = new ElapsedTime();
    private ElapsedTime frTimer = new ElapsedTime();
    private ElapsedTime blTimer = new ElapsedTime();
    private ElapsedTime brTimer = new ElapsedTime();

    @Override
    public void runOpMode() {

        // Hardware map
        frontLeftMotor  = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        backLeftMotor   = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor  = hardwareMap.get(DcMotor.class, "backRightMotor");

        // Reverse left side
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        // Encoder mode
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Init encoder
        flLast = frontLeftMotor.getCurrentPosition();
        frLast = frontRightMotor.getCurrentPosition();
        blLast = backLeftMotor.getCurrentPosition();
        brLast = backRightMotor.getCurrentPosition();

        flTimer.reset();
        frTimer.reset();
        blTimer.reset();
        brTimer.reset();

        telemetry.addLine("Status: Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            // Gamepad
            double fwd    = -gamepad1.left_stick_y;
            double strafe =  gamepad1.left_stick_x;
            double rotate =  gamepad1.right_stick_x;

            double speed;
            if (gamepad1.right_bumper) {
                speed = TURBO_SPEED;
                telemetry.addLine("Speed Mode: TURBO (100%)");
            } else {
                speed = NORMAL_SPEED;
                telemetry.addLine("Speed Mode: NORMAL (70%)");
            }

            drive(fwd, strafe, rotate, speed);

            // ===== RPM =====
            double flRPM = calcRPM(frontLeftMotor,  flTimer, () -> flLast, v -> flLast = v);
            double frRPM = calcRPM(frontRightMotor, frTimer, () -> frLast, v -> frLast = v);
            double blRPM = calcRPM(backLeftMotor,   blTimer, () -> blLast, v -> blLast = v);
            double brRPM = calcRPM(backRightMotor,  brTimer, () -> brLast, v -> brLast = v);


            telemetry.addData("Trái Trên", "%.1f", flRPM);
            telemetry.addData("Phải Trên", "%.1f", frRPM);
            telemetry.addData("Trái Dưới", "%.1f", blRPM);
            telemetry.addData("Phải Dưới", "%.1f", brRPM);

            telemetry.update();
        }
    }

    // ===== MECANUM DRIVE =====
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

    // ===== TÍNH RPM =====
    private double calcRPM(
            DcMotor motor,
            ElapsedTime timer,
            java.util.function.IntSupplier lastGetter,
            java.util.function.IntConsumer lastSetter) {

        int current = motor.getCurrentPosition();
        int deltaTicks = current - lastGetter.getAsInt();
        double dt = timer.seconds();

        lastSetter.accept(current);
        timer.reset();

        if (dt <= 0) return 0;

        return Math.abs(deltaTicks) / TICKS_PER_REV / dt * 60.0;
    }
}
