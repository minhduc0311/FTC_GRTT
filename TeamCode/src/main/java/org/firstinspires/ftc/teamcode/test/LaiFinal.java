package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Lai Final")
public class LaiFinal extends LinearOpMode {

    // ===== DRIVE (MECANUM) =====
    // [CO THE CHINH] toc do lai (NORMAL_SPEED, TURBO_SPEED)
    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    private static final double NORMAL_SPEED = 0.7;
    private static final double TURBO_SPEED = 1.0;
    private static final double DRIVE_DEADBAND = 0.05;

    // ===== FLYWHEEL AIM (SERVO) =====
    // [CO THE CHINH] goc min/max/step va ti le servo
    private Servo aimServo;
    private static final double AIM_ANGLE_MIN = 0.0;
    private static final double AIM_ANGLE_MAX = 120.0;
    private static final double AIM_ANGLE_STEP = 30.0;
    // 90 deg ~ 0.67 in ServoTest => 120 deg ~ 0.89
    private static final double AIM_SERVO_MAX_POS = 0.89;
    private double aimAngleDeg = 0.0;
    private boolean yWasPressed = false;
    private boolean aWasPressed = false;

    // ===== FLYWHEEL MOTOR (HD HEX NO GEARBOX) =====
    // [CO THE CHINH] toc do quay banh da (FLYWHEEL_POWER)
    private DcMotor flywheel;
    private static final double FLYWHEEL_POWER = 1.0;
    private boolean flywheelOn = false;
    private boolean bWasPressed = false;

    // ===== BALL BLOCKER SERVOS =====
    // [CO THE CHINH] goc chan bong va thoi gian mo/dong
    private Servo blockerLeft;
    private Servo blockerRight;
    private static final double BLOCKER_ANGLE_MIN = 0.0;
    private static final double BLOCKER_ANGLE_MAX = 120.0;
    private static final double BLOCKER_SERVO_MAX_POS = 0.89;
    private static final double BLOCKER_OPEN_TIME_SEC = 3.0;
    private static final double BLOCKER_CLOSE_TIME_SEC = 3.0;
    private boolean xWasPressed = false;
    private final ElapsedTime blockerTimer = new ElapsedTime();
    private boolean blockerActive = false;

    // ===== INTAKE =====
    // [CO THE CHINH] toc do hut bong (INTAKE_POWER)
    private DcMotor intakeLeft;
    private DcMotor intakeRight;
    private static final double INTAKE_POWER = 1.0;
    private boolean intakeOn = false;
    private boolean startWasPressed = false;

    @Override
    public void runOpMode() {
        initHardware();

        telemetry.addLine("Lai Final - Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            driveMecanum();
            handleAimServos();
            handleFlywheelMotor();
            handleBlockerServo();
            handleIntake();

            telemetry.update();
            sleep(20);
        }
    }

    private void initHardware() {
        // Drive
        // [CO THE CHINH] ten motor mecanum trong Robot Config
        frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");
        frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        // Brake khi dung
        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Aim servo (chinh goc ban)
        // [CO THE CHINH] ten servo chinh goc trong Robot Config
        aimServo = hardwareMap.get(Servo.class, "aimServo");
        setAimAngle(aimAngleDeg);

        // Flywheel motor
        // [CO THE CHINH] ten motor banh da trong Robot Config
        flywheel = hardwareMap.get(DcMotor.class, "flywheel");
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setDirection(DcMotor.Direction.REVERSE);

        // Blocker servos (chan bong)
        // [CO THE CHINH] ten 2 servo chan bong trong Robot Config
        blockerLeft = hardwareMap.get(Servo.class, "lamgay");
        blockerRight = hardwareMap.get(Servo.class, "ducgay");
        setBlockerAngle(BLOCKER_ANGLE_MIN);

        // Intake
        // [CO THE CHINH] ten 2 motor hut bong trong Robot Config
        intakeLeft = hardwareMap.get(DcMotor.class, "intakeLeft");
        intakeRight = hardwareMap.get(DcMotor.class, "intakeRight");
        intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void driveMecanum() {
        double fwd = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;
        double speed = gamepad1.right_bumper ? TURBO_SPEED : NORMAL_SPEED;

        if (Math.abs(fwd) < DRIVE_DEADBAND &&
                Math.abs(strafe) < DRIVE_DEADBAND &&
                Math.abs(rotate) < DRIVE_DEADBAND) {
            stopAllMotors();
            telemetry.addData("Drive Mode", "BRAKE");
            return;
        }

        double fl = fwd + strafe + rotate;
        double fr = fwd - strafe - rotate;
        double bl = fwd - strafe + rotate;
        double br = fwd + strafe - rotate;

        double max = Math.max(1.0,
                Math.max(Math.abs(fl),
                        Math.max(Math.abs(fr),
                                Math.max(Math.abs(bl), Math.abs(br)))));

        frontLeftMotor.setPower((fl / max) * speed);
        frontRightMotor.setPower((fr / max) * speed);
        backLeftMotor.setPower((bl / max) * speed);
        backRightMotor.setPower((br / max) * speed);

        telemetry.addData("Drive Mode", gamepad1.right_bumper ? "TURBO" : "NORMAL");
    }

    private void stopAllMotors() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }

    private void handleAimServos() {
        boolean yIsPressed = gamepad1.y;
        boolean aIsPressed = gamepad1.a;

        if (yIsPressed && !yWasPressed) {
            aimAngleDeg = Math.min(AIM_ANGLE_MAX, aimAngleDeg + AIM_ANGLE_STEP);
            setAimAngle(aimAngleDeg);
        }

        if (aIsPressed && !aWasPressed) {
            aimAngleDeg = Math.max(AIM_ANGLE_MIN, aimAngleDeg - AIM_ANGLE_STEP);
            setAimAngle(aimAngleDeg);
        }

        yWasPressed = yIsPressed;
        aWasPressed = aIsPressed;

        telemetry.addData("Flywheel Angle (deg)", "%.0f", aimAngleDeg);
    }

    private void setAimAngle(double angleDeg) {
        double pos = (angleDeg / AIM_ANGLE_MAX) * AIM_SERVO_MAX_POS;
        aimServo.setPosition(pos);
    }

    private void handleFlywheelMotor() {
        boolean bIsPressed = gamepad1.b;
        if (bIsPressed && !bWasPressed) {
            flywheelOn = !flywheelOn;
        }
        bWasPressed = bIsPressed;

        double power = flywheelOn ? FLYWHEEL_POWER : 0.0;
        flywheel.setPower(power);
        telemetry.addData("Flywheel Power", "%.1f", power);
        telemetry.addData("Flywheel", flywheelOn ? "ON" : "OFF");
    }

    private void handleBlockerServo() {
        boolean xIsPressed = gamepad1.x;
        if (xIsPressed && !xWasPressed) {
            blockerActive = true;
            blockerTimer.reset();
        }
        xWasPressed = xIsPressed;

        if (blockerActive) {
            double t = blockerTimer.seconds();
            if (t <= BLOCKER_OPEN_TIME_SEC) {
                double angle = (t / BLOCKER_OPEN_TIME_SEC) * BLOCKER_ANGLE_MAX;
                setBlockerAngle(angle);
            } else if (t <= BLOCKER_OPEN_TIME_SEC + BLOCKER_CLOSE_TIME_SEC) {
                double t2 = t - BLOCKER_OPEN_TIME_SEC;
                double angle = BLOCKER_ANGLE_MAX * (1.0 - (t2 / BLOCKER_CLOSE_TIME_SEC));
                setBlockerAngle(angle);
            } else {
                setBlockerAngle(BLOCKER_ANGLE_MIN);
                blockerActive = false;
            }
        }

        telemetry.addData("Blocker Active", blockerActive ? "YES" : "NO");
    }

    private void setBlockerAngle(double angleDeg) {
        double pos = (angleDeg / BLOCKER_ANGLE_MAX) * BLOCKER_SERVO_MAX_POS;
        blockerLeft.setPosition(pos);
        blockerRight.setPosition(pos);
    }

    private void handleIntake() {
        boolean startIsPressed = gamepad1.start;
        if (startIsPressed && !startWasPressed) {
            intakeOn = !intakeOn;
        }
        startWasPressed = startIsPressed;

        double power = intakeOn ? INTAKE_POWER : 0.0;
        intakeLeft.setPower(power);
        intakeRight.setPower(power);
        telemetry.addData("Intake Power", "%.1f", power);
        telemetry.addData("Intake", intakeOn ? "ON" : "OFF");
    }
}

