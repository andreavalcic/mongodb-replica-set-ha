package com.szubp.mongodb_replica_set_ha.db.model;

import java.util.EnumSet;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

public class UidGenerator implements BeforeExecutionGenerator {

	@Override
	public Object generate(
			SharedSessionContractImplementor session,
			Object owner,
			Object currentValue,
			EventType eventType) {

		return java.util.UUID.randomUUID()
				.toString()
				.replace("-", "")
				.substring(0, 23);
	}

	@Override
	public boolean generatedOnExecution() {
		return false;
	}

	@Override
	public boolean generatedOnExecution(Object entity, SharedSessionContractImplementor session) {
		return BeforeExecutionGenerator.super.generatedOnExecution(entity, session);
	}

	@Override
	public boolean generatedBeforeExecution(Object entity, SharedSessionContractImplementor session) {
		return BeforeExecutionGenerator.super.generatedBeforeExecution(entity, session);
	}

	@Override
	public EnumSet<EventType> getEventTypes() {
		return EnumSet.of(EventType.INSERT);
	}

	@Override
	public boolean allowAssignedIdentifiers() {
		return BeforeExecutionGenerator.super.allowAssignedIdentifiers();
	}

	@Override
	public boolean allowMutation() {
		return BeforeExecutionGenerator.super.allowMutation();
	}

	@Override
	public boolean generatesSometimes() {
		return BeforeExecutionGenerator.super.generatesSometimes();
	}

	@Override
	public boolean generatesOnInsert() {
		return BeforeExecutionGenerator.super.generatesOnInsert();
	}

	@Override
	public boolean generatesOnUpdate() {
		return BeforeExecutionGenerator.super.generatesOnUpdate();
	}

	@Override
	public boolean generatesOnForceIncrement() {
		return BeforeExecutionGenerator.super.generatesOnForceIncrement();
	}
}