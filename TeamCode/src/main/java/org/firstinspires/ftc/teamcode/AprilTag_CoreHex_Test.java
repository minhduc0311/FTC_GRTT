package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp(name = "AprilTag CoreHex Test")
public class AprilTag_CoreHex_Test extends LinearOpMode {

    // ===== MOTOR =====
    private DcMotor turret;

    // ===== APRILTAG =====
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {

        // ===== MOTOR SETUP =====
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // ===== APRILTAG SETUP =====
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"),
                aprilTag
        );

        telemetry.addLine("Ready - Put AprilTag in front of camera");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {

            double motorPower = 0.0;

            if (!aprilTag.getDetections().isEmpty()) {

                AprilTagDetection tag = aprilTag.getDetections().get(0);

                double yaw = tag.ftcPose.yaw; // độ

                // Tag lệch bao nhiêu thì quay bấy nhiêu
                motorPower = Range.clip(-yaw * 0.02, -0.4, 0.4);

                telemetry.addData("Tag ID", tag.id);
                telemetry.addData("Yaw (deg)", "%.1f", yaw);
            } else {
                telemetry.addLine("No AprilTag detected");
            }

            turret.setPower(motorPower);

            telemetry.addData("Motor Power", "%.2f", motorPower);
            telemetry.update();
        }
    }
}
