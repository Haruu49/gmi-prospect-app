package com.example.gmiapps.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.gmiapps.eligibility.ProgrammeTrack;

import org.junit.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/** Checks that the programme cards and the checker's tracks point at each other correctly. */
public class ProgrammeRepositoryTest {

    private static final Set<ProgrammeTrack> DIPLOMA_TRACKS = EnumSet.of(
            ProgrammeTrack.DIPLOMA_BEM, ProgrammeTrack.DIPLOMA_MQA_ENGINEERING,
            ProgrammeTrack.DIPLOMA_COMPUTING, ProgrammeTrack.DIPLOMA_CREATIVE_MULTIMEDIA,
            ProgrammeTrack.DIPLOMA_MBOT);

    @Test
    public void sevenCards_gappFiveDiplomaGroupsAndGufp() {
        assertEquals(7, ProgrammeRepository.getAll().size());
        assertEquals(Programme.ID_GAPP, ProgrammeRepository.getAll().get(0).getId());
        assertEquals(Programme.ID_GUFP, ProgrammeRepository.getAll().get(6).getId());
    }

    @Test
    public void everyTrack_opensAnExistingCard() {
        for (ProgrammeTrack track : ProgrammeTrack.values()) {
            assertNotNull(track.name(), ProgrammeRepository.findById(track.getProgrammeId()));
        }
    }

    @Test
    public void eachDiplomaTrack_hasItsOwnCard() {
        Set<String> ids = new HashSet<>();
        for (ProgrammeTrack track : DIPLOMA_TRACKS) {
            assertTrue("Two diploma tracks share a card: " + track.getProgrammeId(),
                    ids.add(track.getProgrammeId()));
        }
    }

    @Test
    public void everyCard_isUsedByTheChecker() {
        Set<String> used = new HashSet<>();
        for (ProgrammeTrack track : ProgrammeTrack.values()) {
            used.add(track.getProgrammeId());
        }
        for (Programme programme : ProgrammeRepository.getAll()) {
            assertTrue(programme.getId(), used.contains(programme.getId()));
        }
    }

    @Test
    public void onlyDiplomaCards_addTheSharedDiplomaRequirements() {
        for (Programme programme : ProgrammeRepository.getAll()) {
            boolean diploma = programme.getId().startsWith("diploma_");
            assertEquals(programme.getId(), diploma, programme.getSharedRequirementsRes() != 0);
        }
    }
}
