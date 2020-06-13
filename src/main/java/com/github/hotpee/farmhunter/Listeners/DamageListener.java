package com.github.hotpee.farmhunter.Listeners;

import com.github.hotpee.farmhunter.Arena.Arena;
import com.github.hotpee.farmhunter.ConfigManager.ConfigManager;
import com.github.hotpee.farmhunter.FarmHunter;
import com.github.hotpee.farmhunter.Teams;
import com.github.hotpee.farmhunter.Util.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DamageListener implements Listener {
    @EventHandler
    public void death(PlayerDeathEvent e){
        Player p = e.getEntity().getPlayer();
        Arena arena = Util.getArena(p);
        if (arena == null){
            return;
        }
        if (!(arena.isStates())){
            return;
        }
        Entity damager = e.getEntity().getKiller();
        if (damager instanceof Explosive){
            for (Player players : arena.getPlayerAmount().keySet()){
                Util.Message(players, ConfigManager.getPrefix() + "&3玩家 &e" + p.getName() + " &e被炸死了");
            }
        } else {
            for (Player players : arena.getPlayerAmount().keySet()){
                Util.Message(players, ConfigManager.getPrefix() + "&3玩家 &e" + damager.getName() + " &3击杀了 &e" + p.getName());
            }
        }

        if (arena.getPlayerAmount().get(p) == Teams.HIDER){
            arena.getPlayerAmount().put(p, Teams.SEEKER);
            Util.MessageTitle(p, "&b", "&4死亡!!! 已成为寻找者");
        }
        if (arena.getPlayerAmount().containsValue(Teams.HIDER)){
            return;
        }
        arena.stopGame();
    }

    @EventHandler
    public void TntDamage(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            Player p = (Player)e.getEntity();
            Arena arena = Util.getArena(p);
            if (arena == null){
                return;
            }
            if (!(arena.isStates())){
                return;
            }
            if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
                if (arena.getPlayerAmount().get(p) == Teams.SEEKER){
                    e.setCancelled(true);
                }
            }
        }
        if (e.getEntity() instanceof Animals){
            Player p = (Player)e.getEntity();
            Arena arena = Util.getArena(p);
            if (arena == null){
                return;
            }
            if (!(arena.isStates())){
                return;
            }
            if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
                e.setCancelled(true);
            }
        }

    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e){
        if (e.isCancelled()) {
            return;
        }
        if (e.getDamager() instanceof Player){
            if (e.getEntity() instanceof Player){
                Player Victim = (Player)e.getEntity();
                Player Attacker = (Player)e.getDamager();
                Arena arena = Util.getArena(Attacker);
                if (arena.getPlayerAmount().get(Attacker) == arena.getPlayerAmount().get(Victim)){
                    Util.Message(Attacker, ConfigManager.getPrefix() + "&e请不要攻击你的队友!!");
                    e.setCancelled(true);
                }
            }
        }
        if (e.getEntity() instanceof Animals){
            Player Attacker = (Player)e.getDamager();
            Arena arena = Util.getArena(Attacker);
            if (arena == null){
                return;
            }
            if (!(arena.isStates())){
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void regain(EntityRegainHealthEvent e){
        if (e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            Arena arena = Util.getArena(p);
            if (arena == null){
                return;
            }
            if (!(arena.isStates())){
                return;
            }
            if (arena.getPlayerAmount().get(p) == Teams.HIDER){
                e.setCancelled(true);
                e.setAmount(0);
            }
        }
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        Arena arena = Util.getArena(p);
        if (arena == null){
            return;
        }
        if (!(arena.isStates())){
            return;
        }
        Util.MessageTitle(p, "&b", "&e已重生!");
        Bukkit.getScheduler().runTaskLater(FarmHunter.getIns(),()->p.teleport(arena.getSeekerSpawn()),10L);
    }
}
