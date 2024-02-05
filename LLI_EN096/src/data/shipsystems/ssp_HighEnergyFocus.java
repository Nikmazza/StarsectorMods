package data.shipsystems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;
import data.SSP_NegativeExplosionVisual;
import data.SSP_NegativeExplosionVisual.*;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ssp_HighEnergyFocus extends BaseShipSystemScript {
	private final ArrayList<DamagingProjectileAPI> projList=new ArrayList<>();
	private int pl=0;
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		ShipAPI ship=null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return; }
		//极化判断
		float P0=1f;
		if(ship.getVariant().hasHullMod("ssp_LongerRange")){P0=1.5f;}
		stats.getEnergyWeaponFluxCostMod().modifyMult(id,P0);
		stats.getBeamWeaponDamageMult().modifyMult(id,1.5f*P0);
		//记录弹丸
		List<DamagingProjectileAPI> getProj=Global.getCombatEngine().getProjectiles();
		if(effectLevel<1 && state==State.IN){
			for(DamagingProjectileAPI p:getProj){
				if(p!=null && p.getSource()!=null && p.getWeapon()!=null){
				if(p.getSource()==ship && p.getWeapon().getSpec().getType() == WeaponAPI.WeaponType.ENERGY){
					boolean can_recorded=false;
					for(WeaponAPI w:ship.getAllWeapons()){
						if(w.getSpec().getProjectileSpec()==p.getProjectileSpec()){
							can_recorded=true;
						}
					}
					if(can_recorded && p.getCustomData()!=null && !p.getCustomData().containsKey("ssp_HighEnergyFocus_recorded")){
						projList.add(p);
						p.setCustomData("ssp_HighEnergyFocus_recorded",false);
					}
					if(can_recorded && p.getCustomData()==null){
						projList.add(p);
						p.setCustomData("ssp_HighEnergyFocus_recorded",false);
					}
				}
			}
			}
		}
		if(effectLevel<1 && state==State.OUT){
			if(projList.size()==0)return;
			if((float)1/projList.size()*pl<(1-effectLevel)){
				//极化判断
				float P1=1f;
				if(ship.getVariant().hasHullMod("ssp_LongerRange")){P1=0.1f;}
				//生成弹幕
				DamagingProjectileAPI damagingProjectileAPI=projList.get(pl);
				if(damagingProjectileAPI==null) return;
				//检测发射特效
				OnFireEffectPlugin onFireEffect=null;
				if(damagingProjectileAPI instanceof MissileAPI){
					onFireEffect =((MissileAPI) damagingProjectileAPI).getSpec().getOnFireEffect();
				} else {
					onFireEffect = damagingProjectileAPI.getProjectileSpec().getOnFireEffect();
				}
				Vector2f Point=MathUtils.getRandomPointInCircle(ship.getLocation(), ship.getCollisionRadius());
				//取得发射朝向
				float Angle=0;
				if(ship.getShipTarget()!=null){
					Angle= Misc.getAngleInDegrees(Point,ship.getShipTarget().getLocation());
				}else if(ship.getShipTarget()==null && ship.getMouseTarget()!=null){
					Angle= Misc.getAngleInDegrees(Point,ship.getMouseTarget());
				}
				//生成发射点特效
				SSP_NEParams PARAMS =new SSP_NEParams();
				PARAMS.hitGlowSizeMult = 0.75f;
				PARAMS.spawnHitGlowAt = 0f;
				PARAMS.noiseMag = 1f;
				PARAMS.fadeIn = 0.1f;
				PARAMS.underglow = new Color(70, 70, 105, 20);
				PARAMS.withHitGlow = false;
				PARAMS.radius = 3;
				PARAMS.invertAlpha=50;
				PARAMS.color = new Color(100, 100, 145, 20);
				PARAMS.renderCircleColor = Color.white;
				GenShit_Start(Point,PARAMS);
				//生成弹丸
				DamagingProjectileAPI spawnProjectile = (DamagingProjectileAPI) Global.getCombatEngine().spawnProjectile(
						ship,
						damagingProjectileAPI.getWeapon(),
						damagingProjectileAPI.getWeapon().getId(),
						Point,
						Angle + MathUtils.getRandomNumberInRange(-15*P1, 15*P1),
						ship.getVelocity()
				);
				if (onFireEffect != null) {
					onFireEffect.onFire(spawnProjectile, damagingProjectileAPI.getWeapon(), Global.getCombatEngine());
				}
				pl++;
			}
		}
	}
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship=null;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
		} else {
			return; }
		stats.getEnergyWeaponFluxCostMod().unmodify(id);
		stats.getBeamWeaponDamageMult().unmodify(id);
		projList.clear();
		pl=0;
	}
	
	public StatusData getStatusData(int index, State state, float effectLevel) {
		if (index == 0) {
			return new StatusData(SSPI18nUtil.getShipSystemString("ssp_HighEnergyFocus")+projList.size(), false);
		}
		return null;
	}
	@Override
	public float getInOverride(ShipAPI ship) {
		if (ship != null) {
			if (ship.getVariant().hasHullMod("ssp_ShortRange")){return 4.5f;}}
		return -1;
	}
	public static void GenShit_Start(Vector2f loc, SSP_NEParams params) {
		CombatEngineAPI engine = Global.getCombatEngine();
		CombatEntityAPI prev = null;
		for (int i = 0; i < 2; i++) {
			SSP_NEParams p = params.clone();
			p.radius *= 0.75f + 0.5f * (float) Math.random();

			p.withHitGlow = prev == null;

			CombatEntityAPI e = engine.addLayeredRenderingPlugin(new SSP_NegativeExplosionVisual(p));
			e.getLocation().set(loc);

			if (prev != null) {
				float dist = Misc.getDistance(prev.getLocation(), loc);
				Vector2f vel = Misc.getUnitVectorAtDegreeAngle(Misc.getAngleInDegrees(loc, prev.getLocation()));
				vel.scale(dist / (p.fadeIn + p.fadeOut) * 0.7f);
				e.getVelocity().set(vel);
			}
			prev = e;
		}
	}
}
