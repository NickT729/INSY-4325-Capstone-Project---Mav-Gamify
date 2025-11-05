// Level and XP calculation utilities
// Levels go from 1 to 20
// XP requirements increase with each level

/**
 * Calculate required XP for a given level
 * Level 1: 0 XP (starting level)
 * Level 2: 100 XP
 * Level 3: 250 XP
 * Level 4: 450 XP
 * Level 5: 700 XP
 * ... and so on with increasing increments
 * Level 20: 10,000 XP
 */
export function getXPForLevel(level) {
  if (level <= 1) return 0;
  if (level === 2) return 100;
  if (level === 3) return 250;
  if (level === 4) return 450;
  if (level === 5) return 700;
  if (level === 6) return 1000;
  if (level === 7) return 1350;
  if (level === 8) return 1750;
  if (level === 9) return 2200;
  if (level === 10) return 2700;
  if (level === 11) return 3250;
  if (level === 12) return 3850;
  if (level === 13) return 4500;
  if (level === 14) return 5200;
  if (level === 15) return 5950;
  if (level === 16) return 6750;
  if (level === 17) return 7600;
  if (level === 18) return 8500;
  if (level === 19) return 9450;
  if (level >= 20) return 10000;
  
  // Fallback calculation for levels beyond defined
  return 10000 + (level - 20) * 500;
}

/**
 * Calculate level based on current XP
 * Returns level between 1 and 20
 */
export function calculateLevel(xp) {
  if (xp < 0) return 1;
  if (xp < 100) return 1;
  if (xp < 250) return 2;
  if (xp < 450) return 3;
  if (xp < 700) return 4;
  if (xp < 1000) return 5;
  if (xp < 1350) return 6;
  if (xp < 1750) return 7;
  if (xp < 2200) return 8;
  if (xp < 2700) return 9;
  if (xp < 3250) return 10;
  if (xp < 3850) return 11;
  if (xp < 4500) return 12;
  if (xp < 5200) return 13;
  if (xp < 5950) return 14;
  if (xp < 6750) return 15;
  if (xp < 7600) return 16;
  if (xp < 8500) return 17;
  if (xp < 9450) return 18;
  if (xp < 10000) return 19;
  return 20; // Max level
}

/**
 * Calculate XP needed for next level
 */
export function getXPNeededForNextLevel(currentXP, currentLevel) {
  if (currentLevel >= 20) return 0; // Already at max level
  
  const nextLevel = currentLevel + 1;
  const xpForNextLevel = getXPForLevel(nextLevel);
  return Math.max(0, xpForNextLevel - currentXP);
}

/**
 * Calculate XP progress percentage for current level
 */
export function getLevelProgress(currentXP, currentLevel) {
  if (currentLevel >= 20) return 100;
  
  const xpForCurrentLevel = getXPForLevel(currentLevel);
  const xpForNextLevel = getXPForLevel(currentLevel + 1);
  const xpInCurrentLevel = currentXP - xpForCurrentLevel;
  const xpNeededForLevel = xpForNextLevel - xpForCurrentLevel;
  
  return Math.min(100, Math.round((xpInCurrentLevel / xpNeededForLevel) * 100));
}








