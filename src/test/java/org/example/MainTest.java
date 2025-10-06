package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MainTest {

    private static class TestableCoffeeMachine extends Main.CoffeeMachine {
        void forceState(State state) {
            this.currentState = state;
        }
    }

    private TestableCoffeeMachine coffeeMachine;

    @BeforeEach
    void setUp() {
        Main.CoffeeMachine.setNumberCoffees(0);
        coffeeMachine = new TestableCoffeeMachine();
    }

    @Test
    void testPowerOnTriggersMaintenanceWhenThresholdReached() {
        Main.CoffeeMachine.setNumberCoffees(3);
        coffeeMachine.powerOn();

        assertSame(Main.CoffeeMachine.State.IDLE, coffeeMachine.getCurrentState());
        assertEquals(0, Main.CoffeeMachine.getNumberCoffees());
    }

    @Test
    void testPowerOffSetsReadyState() {
        coffeeMachine.powerOff();
        assertSame(Main.CoffeeMachine.State.READY, coffeeMachine.getCurrentState());
    }

    @Test
    void testSelectCoffeeFromIdleMovesToReady() {
        coffeeMachine.powerOn();
        coffeeMachine.selectCoffee();

        assertSame(Main.CoffeeMachine.State.READY, coffeeMachine.getCurrentState());
    }

    @Test
    void testSelectCoffeeTriggersMaintenanceBeforeReady() {
        coffeeMachine.powerOn();
        Main.CoffeeMachine.setNumberCoffees(3);

        coffeeMachine.selectCoffee();

        assertSame(Main.CoffeeMachine.State.READY, coffeeMachine.getCurrentState());
        assertEquals(0, Main.CoffeeMachine.getNumberCoffees());
    }

    @Test
    void testSelectCoffeeWhenNotIdleTriggersError() {
        coffeeMachine.selectCoffee();
        assertSame(Main.CoffeeMachine.State.ERROR, coffeeMachine.getCurrentState());
    }

    @Test
    void testStartBrewingFromReadyCompletesCycle() {
        coffeeMachine.powerOn();
        coffeeMachine.selectCoffee();

        coffeeMachine.startBrewing();

        assertSame(Main.CoffeeMachine.State.IDLE, coffeeMachine.getCurrentState());
        assertEquals(1, Main.CoffeeMachine.getNumberCoffees());
    }

    @Test
    void testStartBrewingFromInvalidStateEntersError() {
        coffeeMachine.startBrewing();
        assertSame(Main.CoffeeMachine.State.ERROR, coffeeMachine.getCurrentState());
    }

    @Test
    void testStartBrewingWhenMaintenanceRequired() {
        coffeeMachine.forceState(Main.CoffeeMachine.State.READY);
        Main.CoffeeMachine.setNumberCoffees(3);

        coffeeMachine.startBrewing();

        assertSame(Main.CoffeeMachine.State.IDLE, coffeeMachine.getCurrentState());
        assertEquals(0, Main.CoffeeMachine.getNumberCoffees());
    }

    @Test
    void testHeatingWaterTransitionsWhenBrewing() {
        coffeeMachine.forceState(Main.CoffeeMachine.State.BREWING);

        coffeeMachine.heatingWater();

        assertSame(Main.CoffeeMachine.State.HEATING_WATER, coffeeMachine.getCurrentState());
    }

    @Test
    void testHeatingWaterWhenNotBrewingCausesError() {
        coffeeMachine.heatingWater();
        assertSame(Main.CoffeeMachine.State.ERROR, coffeeMachine.getCurrentState());
    }

    @Test
    void testGrindingBeansTransitionsWhenHeatingWater() {
        coffeeMachine.forceState(Main.CoffeeMachine.State.HEATING_WATER);

        coffeeMachine.grindingBeans();

        assertSame(Main.CoffeeMachine.State.GRINDING_BEANS, coffeeMachine.getCurrentState());
    }

    @Test
    void testGrindingBeansWhenNotHeatingWaterCausesError() {
        coffeeMachine.grindingBeans();
        assertSame(Main.CoffeeMachine.State.ERROR, coffeeMachine.getCurrentState());
    }

    @Test
    void testPerformMaintenanceWithoutRequirement() {
        coffeeMachine.performMaintenance();
        assertSame(Main.CoffeeMachine.State.OFF, coffeeMachine.getCurrentState());
    }

    @Test
    void testCoffeeMachineErrorSetsErrorState() {
        coffeeMachine.coffeeMachineError();
        assertSame(Main.CoffeeMachine.State.ERROR, coffeeMachine.getCurrentState());
    }

    @Test
    void testMainMethodRuns() {
        Main.main(new String[0]);
        assertEquals(0, Main.CoffeeMachine.getNumberCoffees());
    }
}
