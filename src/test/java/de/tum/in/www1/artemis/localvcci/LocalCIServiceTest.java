package de.tum.in.www1.artemis.localvcci;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import de.tum.in.www1.artemis.AbstractSpringIntegrationLocalCILocalVCTest;
import de.tum.in.www1.artemis.domain.Course;
import de.tum.in.www1.artemis.domain.ProgrammingExercise;
import de.tum.in.www1.artemis.domain.participation.ProgrammingExerciseStudentParticipation;
import de.tum.in.www1.artemis.service.connectors.ci.ContinuousIntegrationService.BuildStatus;

class LocalCIServiceTest extends AbstractSpringIntegrationLocalCILocalVCTest {

    private static final String TEST_PREFIX = "localciservice";

    @Test
    @WithMockUser(username = "instructor1", roles = "INSTRUCTOR")
    void testReturnCorrectBuildStatus() {
        database.addUsers(TEST_PREFIX, 1, 0, 0, 1);
        Course course = database.addCourseWithOneProgrammingExercise();
        ProgrammingExercise exercise = database.getFirstExerciseWithType(course, ProgrammingExercise.class);
        ProgrammingExerciseStudentParticipation participation = database.addStudentParticipationForProgrammingExercise(exercise, TEST_PREFIX + "student1");
        assertThat(continuousIntegrationService.getBuildStatus(participation)).isEqualTo(BuildStatus.INACTIVE);
    }

    @Test
    void testHealth() {
        var health = continuousIntegrationService.health();
        assertThat(health.isUp()).isTrue();
    }

    @Test
    void testUnsupportedMethods() {
        continuousIntegrationService.recreateBuildPlansForExercise(null);
        continuousIntegrationService.givePlanPermissions(null, null);
        continuousIntegrationService.enablePlan(null, null);
        continuousIntegrationService.updatePlanRepository(null, null, null, null, null, null, null, null);
        assertThat(continuousIntegrationService.getPlanKey(null)).isNull();
        assertThat(continuousIntegrationService.getWebHookUrl(null, null)).isEmpty();
        ResponseEntity<byte[]> latestArtifactResponse = continuousIntegrationService.retrieveLatestArtifact(null);
        assertThat(latestArtifactResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(latestArtifactResponse.getBody()).hasSize(0);
    }
}