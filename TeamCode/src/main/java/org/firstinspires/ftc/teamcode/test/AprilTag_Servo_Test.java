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

@TeleOp(name = "Test: AprilTag CoreHex Search")
public class AprilTag_Servo_Test extends LinearOpMode {

    private static final String CAMERA_NAME = "Webcam 1";
    private static final String TURRET_MOTOR_NAME = "turret";

    private static final double SEARCH_POWER = 0.3;
    private static final double TURN_P = 0.02;
    private static final double MAX_TURN_POWER = 0.4;
    private static final double DEAD_BAND_DEG = 1.5;
    private static final double TURN_DIRECTION = 1.0; // doi thanh -1 neu quay nguoc

    private DcMotor turretMotor;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {
        turretMotor = hardwareMap.get(DcMotor.class, TURRET_MOTOR_NAME);
        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, CAMERA_NAME),
                aprilTag
        );

        telemetry.addLine("Ready - servo se quay khi khong thay tag");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTag.getDetections();

            double servoPower;
            AprilTagDetection best = chooseBestDetection(detections);

            if (best == null) {
                servoPower = SEARCH_POWER;
                telemetry.addLine("No AprilTag detected");
            } else {
                double bearing = best.ftcPose.bearing;
                if (Math.abs(bearing) <= DEAD_BAND_DEG) {
                    servoPower = 0.0; // dung quay khi da nhin thang vao tag
                } else {
                    servoPower = Range.clip(bearing * TURN_P * TURN_DIRECTION,
                            -MAX_TURN_POWER, MAX_TURN_POWER);
                }
                telemetry.addData("Tag ID", best.id);
                telemetry.addData("Bearing (deg)", "%.1f", bearing);
            }

            turretMotor.setPower(servoPower);
            telemetry.addData("Motor Power", "%.2f", servoPower);
            telemetry.update();

            sleep(20);
        }

        visionPortal.close();
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

