package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@Autonomous(name = "Auto Park 30s Blue (C270)")
public class AutoPark30_C270_Blue extends LinearOpMode {

    // ===== CO THE CHINH: TEN CAMERA =====
    private static final String CAMERA_NAME = "Webcam 1"; // Logitech C270

    // ===== CO THE CHINH: TEN MOTOR MECANUM =====
    private static final String FL_NAME = "frontLeftMotor";
    private static final String FR_NAME = "frontRightMotor";
    private static final String BL_NAME = "backLeftMotor";
    private static final String BR_NAME = "backRightMotor";

    // ===== CO THE CHINH: TAG ID FTC (BLUE) =====
    private static final int TAG_LEFT = 4;
    private static final int TAG_CENTER = 5;
    private static final int TAG_RIGHT = 6;

    // ===== CO THE CHINH: TOC DO/THOI GIAN =====
    private static final double DRIVE_POWER = 0.4;
    private static final double STRAFE_POWER = 0.4;
    private static final double DRIVE_TIME_SEC = 2.0;
    private static final double STRAFE_TIME_SEC = 1.0;
    private static final double TOTAL_AUTO_TIME_SEC = 30.0;

    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    private int parkPosition = 0; // -1 = LEFT, 0 = CENTER, 1 = RIGHT

    @Override
    public void runOpMode() {
        initHardware();
        initCamera();

        // Quan sat tag truoc khi start
        while (!isStarted() && !isStopRequested()) {
            updateParkPosition();
            telemetry.addData("Park", parkPositionLabel());
            telemetry.update();
            sleep(20);
        }

        waitForStart();

        ElapsedTime timer = new ElapsedTime();

        while (opModeIsActive() && timer.seconds() < TOTAL_AUTO_TIME_SEC) {
            double t = timer.seconds();

            if (t < DRIVE_TIME_SEC) {
                drive(DRIVE_POWER, 0.0, 0.0);
                telemetry.addData("Phase", "Drive Forward");
            } else if (t < DRIVE_TIME_SEC + STRAFE_TIME_SEC) {
                drive(0.0, STRAFE_POWER * parkPosition, 0.0);
                telemetry.addData("Phase", "Strafe " + parkPositionLabel());
            } else {
                stopAllMotors();
                telemetry.addData("Phase", "Parked");
            }

            telemetry.addData("Time", "%.1f/%.0f", t, TOTAL_AUTO_TIME_SEC);
            telemetry.update();
            sleep(20);
        }

        stopAllMotors();
        visionPortal.close();
    }

    private void updateParkPosition() {
        List<AprilTagDetection> detections = aprilTag.getDetections();
        for (AprilTagDetection detection : detections) {
            int id = detection.id;
            if (id == TAG_LEFT) {
                parkPosition = -1;
                return;
            } else if (id == TAG_CENTER) {
                parkPosition = 0;
                return;
            } else if (id == TAG_RIGHT) {
                parkPosition = 1;
                return;
            }
        }
    }

    private String parkPositionLabel() {
        if (parkPosition < 0) return "LEFT";
        if (parkPosition > 0) return "RIGHT";
        return "CENTER";
    }

    private void initHardware() {
        frontLeftMotor = hardwareMap.get(DcMotor.class, FL_NAME);
        frontRightMotor = hardwareMap.get(DcMotor.class, FR_NAME);
        backLeftMotor = hardwareMap.get(DcMotor.class, BL_NAME);
        backRightMotor = hardwareMap.get(DcMotor.class, BR_NAME);

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void initCamera() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, CAMERA_NAME),
                aprilTag
        );
    }

    private void drive(double fwd, double strafe, double rotate) {
        double fl = fwd + strafe + rotate;
        double fr = fwd - strafe - rotate;
        double bl = fwd - strafe + rotate;
        double br = fwd + strafe - rotate;

        double max = Math.max(1.0,
                Math.max(Math.abs(fl),
                        Math.max(Math.abs(fr),
                                Math.max(Math.abs(bl), Math.abs(br)))));

        frontLeftMotor.setPower(fl / max);
        frontRightMotor.setPower(fr / max);
        backLeftMotor.setPower(bl / max);
        backRightMotor.setPower(br / max);
    }

    private void stopAllMotors() {
        frontLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backLeftMotor.setPower(0);
        backRightMotor.setPower(0);
    }
}

