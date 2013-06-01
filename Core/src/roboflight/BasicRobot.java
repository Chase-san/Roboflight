package roboflight;

import roboflight.events.BulletHitEvent;
import roboflight.events.HitByBulletEvent;
import roboflight.events.MissileUpdateEvent;
import roboflight.events.RobotDeathEvent;
import roboflight.events.RobotUpdateEvent;
import roboflight.util.Vector;

public class BasicRobot implements Robot, RobotPeer {
	private RobotPeer peer;

	@Override
	public double getBulletHeat() {
		return peer.getBulletHeat();
	}

	@Override
	public double getEnergy() {
		return peer.getEnergy();
	}

	@Override
	public String getName() {
		return peer.getName();
	}

	@Override
	public Vector getPosition() {
		return peer.getPosition();
	}

	@Override
	public long getTime() {
		return peer.getTime();
	}

	@Override
	public Vector getVelocity() {
		return peer.getVelocity();
	}

	@Override
	public void onBattleStarted() {
	}

	@Override
	public void onBulletHit(BulletHitEvent e) {
	}

	@Override
	public void onHitByBullet(HitByBulletEvent e) {
	}

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
	}

	@Override
	public void onTurnEnded() {
	}

	@Override
	public void onTurnStarted() {
	}

	@Override
	public void onRobotUpdate(RobotUpdateEvent e) {
	}

	@Override
	public Bullet setFireBullet(Vector target) {
		return peer.setFireBullet(target);
	}

	@Override
	public Missile setFireMissile(Vector target) {
		return peer.setFireMissile(target);
	}

	@Override
	public final void setRobotPeer(RobotPeer peer) {
		this.peer = peer;
	}

	@Override
	public void setThrust(Vector thrust) {
		peer.setThrust(thrust);
	}

	@Override
	public void onMissileUpdate(MissileUpdateEvent e) {
		
	}
}
