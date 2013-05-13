package com.compass.domain.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ProfileTest {

    @Test
    public void keeps_its_name() {
        assertThat(Profile.of("Explorer").name(), is("Explorer"));
    }

    @Test
    public void same_name_is_equal() {
        assertThat(Profile.of("Explorer"), is(Profile.of("Explorer")));
        assertThat(Profile.of("Explorer").hashCode(), is(Profile.of("Explorer").hashCode()));
    }

    @Test
    public void different_name_is_not_equal() {
        assertThat(Profile.of("Explorer"), is(not(Profile.of("Expert"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejects_a_blank_name() {
        Profile.of(" ");
    }
}
