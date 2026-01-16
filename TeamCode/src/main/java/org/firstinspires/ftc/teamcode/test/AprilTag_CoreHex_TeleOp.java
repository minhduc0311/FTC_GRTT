package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "Test: AprilTag CoreHex Turret")
public class AprilTag_CoreHex_TeleOp extends LinearOpMode {

    // ===== CONFIG =====
    private static final String CAMERA_NAME = "Webcam 1"; // Ten webcam trong Robot Config
    private static final String TURRET_MOTOR_NAME = "turret"; // Ten dong co Core Hex

    private static final double TURN_P = 0.02; // He so P cho goc lech (deg)
    private static final double MAX_TURN_POWER = 0.4;
    private static final double DEAD_BAND_DEG = 1.5;
    private static final double TURN_DIRECTION = 1.0; // Doi thanh -1 neu quay nguoc

    // ===== HARDWARE =====
    private DcMotor turret;

    // ===== VISION =====
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {
        initHardware();
        initAprilTag();

        telemetry.addLine("Ready - Can AprilTag vao tam camera");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTag.getDetections();

            double motorPower = 0.0;
            AprilTagDetection best = chooseBestDetection(detections);

            if (best != null) {
                double bearing = best.ftcPose.bearing; // do lech trai/phai so voi tam camera

                if (Math.abs(bearing) > DEAD_BAND_DEG) {
                    motorPower = Range.clip(bearing * TURN_P * TURN_DIRECTION,
                            -MAX_TURN_POWER, MAX_TURN_POWER);
                }

                telemetry.addData("Tag ID", best.id);
                telemetry.addData("Bearing (deg)", "%.1f", bearing);
                telemetry.addData("Range (in)", "%.1f", best.ftcPose.range);
            } else {
                telemetry.addLine("No AprilTag detected");
            }

            turret.setPower(motorPower);

            telemetry.addData("Turret Power", "%.2f", motorPower);
            telemetry.update();

            sleep(20);
        }

        visionPortal.close();
    }

    private void initHardware() {
        turret = hardwareMap.get(DcMotor.class, TURRET_MOTOR_NAME);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private void initAprilTag() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, CAMERA_NAME),
                aprilTag
        );
    }

    private AprilTagDetection chooseBestDetection(List<AprilTagDetection> detections) {
        if (detections == null || detections.isEmpty()) {
            return null;
        }

        AprilTagDetection best = detections.get(0);
        double bestScore = Math.abs(best.ftcPose.bearing);

        for (int i = 1; i < detections.size(); i++) {
            AprilTagDetection d = detections.get(i);
            double score = Math.abs(d.ftcPose.bearing);
            if (score < bestScore) {
                best = d;
                bestScore = score;
            }
        }

        return best;
    }
}



