package com.brazilbow;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ArrowTrailListener implements Listener {

    private final BowManager bowManager;
    private final Set<UUID> brazilArrows = new HashSet<>();
    private final Set<UUID> notifiedPlayers = new HashSet<>();
    private final JavaPlugin plugin;

    public ArrowTrailListener(BowManager bowManager, JavaPlugin plugin) {
        this.bowManager = bowManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (bowManager.isBrazilBow(event.getBow())) {
            if (event.getProjectile() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getProjectile();
                brazilArrows.add(arrow.getUniqueId());

                startParticleTrail(arrow, event.getEntity());
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            UUID arrowId = arrow.getUniqueId();

            if (brazilArrows.contains(arrowId)) {
                brazilArrows.remove(arrowId);
                Location hitLoc = arrow.getLocation();

                spawnImpactParticles(hitLoc);
            }
        }
    }

    private void startParticleTrail(Arrow arrow, Entity shooter) {
        new BukkitRunnable() {
            private int ticks = 0;
            private final int maxTicks = 120;

            @Override
            public void run() {
                if (!arrow.isValid() || ticks >= maxTicks || arrow.isOnGround()) {
                    brazilArrows.remove(arrow.getUniqueId());
                    this.cancel();
                    return;
                }

                Location loc = arrow.getLocation();
                spawnTrailParticles(loc);
                checkNearbyPlayers(arrow, shooter);

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void spawnTrailParticles(Location location) {
        if (location.getWorld() == null) {
            return;
        }

        Particle.DustOptions yellow = new Particle.DustOptions(Color.fromBGR(0, 255, 255), 1.5f);
        Particle.DustOptions green = new Particle.DustOptions(Color.fromBGR(0, 128, 0), 1.5f);
        Particle.DustOptions blue = new Particle.DustOptions(Color.fromBGR(255, 0, 0), 1.5f);

        location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, yellow);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, green);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 5, blue);
    }

    private void spawnImpactParticles(Location location) {
        if (location.getWorld() == null) {
            return;
        }

        Particle.DustOptions yellow = new Particle.DustOptions(Color.fromBGR(0, 255, 255), 2.0f);
        Particle.DustOptions green = new Particle.DustOptions(Color.fromBGR(0, 128, 0), 2.0f);
        Particle.DustOptions blue = new Particle.DustOptions(Color.fromBGR(255, 0, 0), 2.0f);

        // Large impact burst
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 40, 1.0, 1.0, 1.0, yellow);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 40, 1.0, 1.0, 1.0, green);
        location.getWorld().spawnParticle(Particle.REDSTONE, location, 40, 1.0, 1.0, 1.0, blue);

        // Add extra visual effects with fireworks spark
        location.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, location, 8, 1.0, 1.0, 1.0);
    }

    private void checkNearbyPlayers(Arrow arrow, Entity shooter) {
        Location arrowLoc = arrow.getLocation();
        if (arrowLoc.getWorld() == null) {
            return;
        }

        for (Entity entity : arrow.getNearbyEntities(3.0, 3.0, 3.0)) {
            if (entity instanceof Player && entity != shooter) {
                Player player = (Player) entity;
                UUID playerId = player.getUniqueId();

                if (!notifiedPlayers.contains(playerId)) {
                    notifiedPlayers.add(playerId);
                    player.sendMessage(ChatColor.GREEN + "You have been taken to Brazil!");

                    // Remove from the set after a while so it can trigger again
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            notifiedPlayers.remove(playerId);
                        }
                    }.runTaskLater(plugin, 40); // 2 seconds
                }
            }
        }
    }
}