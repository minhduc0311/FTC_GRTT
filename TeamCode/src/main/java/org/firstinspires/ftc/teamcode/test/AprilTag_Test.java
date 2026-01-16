package org.firstinspires.ftc.teamcode.test;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "Test: AprilTag Camera")
public class AprilTag_Test extends LinearOpMode {

    private static final String CAMERA_NAME = "Webcam 1";

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, CAMERA_NAME),
                aprilTag
        );

        telemetry.addLine("Ready - dua AprilTag vao camera");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTag.getDetections();

            if (detections.isEmpty()) {
                telemetry.addLine("No AprilTag detected");
            } else {
                AprilTagDetection tag = detections.get(0);
                telemetry.addData("Tag ID", tag.id);
                telemetry.addData("Range (in)", "%.1f", tag.ftcPose.range);
                telemetry.addData("Bearing (deg)", "%.1f", tag.ftcPose.bearing);
                telemetry.addData("Yaw (deg)", "%.1f", tag.ftcPose.yaw);
            }

            telemetry.update();
            sleep(20);
        }

        visionPortal.close();
    }
}



