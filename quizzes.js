import express from 'express';
import { calculateLevel } from '../utils/levelCalculator.js';
import { saveDatabase } from '../db.js';

const router = express.Router();

// Get all quizzes
router.get('/', (req, res) => {
  const db = req.app.locals.db;

  try {
    const quizzes = db.prepare(`
      SELECT q.*, 
        (SELECT COUNT(*) FROM quiz_questions WHERE quiz_id = q.id) as question_count
      FROM quizzes q
      WHERE q.is_public = 1
      ORDER BY q.created_at DESC
    `).all();

    const quizzesWithQuestions = quizzes.map(quiz => {
      const questions = db.prepare(`
        SELECT qq.*, 
          GROUP_CONCAT(qc.choice_text || '|' || qc.choice_index, '||') as choices_data
        FROM quiz_questions qq
        LEFT JOIN quiz_choices qc ON qq.id = qc.question_id
        WHERE qq.quiz_id = ?
        GROUP BY qq.id
        ORDER BY qq.question_order
      `).all(quiz.id);

      const formattedQuestions = questions.map(q => {
        const choices = q.choices_data ? q.choices_data.split('||').map(c => {
          const [text, index] = c.split('|');
          return { text, index: parseInt(index) };
        }).sort((a, b) => a.index - b.index).map(c => c.text) : [];

        return {
          text: q.question_text,
          choices,
          answer: q.correct_answer_index,
        };
      });

      return {
        id: quiz.id.toString(),
        title: quiz.title,
        questions: formattedQuestions,
        completedBy: [],
      };
    });

    res.json(quizzesWithQuestions);
  } catch (error) {
    console.error('Get quizzes error:', error);
    res.status(500).json({ error: 'Failed to get quizzes' });
  }
});

// Get quiz by ID
router.get('/:quizId', (req, res) => {
  const { quizId } = req.params;
  const db = req.app.locals.db;

  try {
    const quiz = db.prepare('SELECT * FROM quizzes WHERE id = ?').get(quizId);
    if (!quiz) {
      return res.status(404).json({ error: 'Quiz not found' });
    }

    const questions = db.prepare(`
      SELECT qq.*, 
        GROUP_CONCAT(qc.choice_text || '|' || qc.choice_index, '||') as choices_data
      FROM quiz_questions qq
      LEFT JOIN quiz_choices qc ON qq.id = qc.question_id
      WHERE qq.quiz_id = ?
      GROUP BY qq.id
      ORDER BY qq.question_order
    `).all(quizId);

    const formattedQuestions = questions.map(q => {
      const choices = q.choices_data ? q.choices_data.split('||').map(c => {
        const [text, index] = c.split('|');
        return { text, index: parseInt(index) };
      }).sort((a, b) => a.index - b.index).map(c => c.text) : [];

      return {
        text: q.question_text,
        choices,
        answer: q.correct_answer_index,
      };
    });

    res.json({
      id: quiz.id.toString(),
      title: quiz.title,
      questions: formattedQuestions,
      completedBy: [],
    });
  } catch (error) {
    console.error('Get quiz error:', error);
    res.status(500).json({ error: 'Failed to get quiz' });
  }
});

// Create quiz
router.post('/', (req, res) => {
  const { title, questions, createdBy } = req.body;
  const db = req.app.locals.db;

  try {
    if (!title || !questions || questions.length === 0) {
      return res.status(400).json({ error: 'Title and questions are required' });
    }

    const insertQuiz = db.prepare('INSERT INTO quizzes (title, created_by, is_public) VALUES (?, ?, 1)');
    const result = insertQuiz.run(title, createdBy || null);
    const quizId = result.lastInsertRowid;

    const insertQuestion = db.prepare(`
      INSERT INTO quiz_questions (quiz_id, question_text, correct_answer_index, question_order)
      VALUES (?, ?, ?, ?)
    `);
    const insertChoice = db.prepare(`
      INSERT INTO quiz_choices (question_id, choice_text, choice_index)
      VALUES (?, ?, ?)
    `);

    questions.forEach((question, index) => {
      const questionResult = insertQuestion.run(
        quizId,
        question.text,
        question.answer,
        index
      );
      const questionId = questionResult.lastInsertRowid;

      question.choices.forEach((choice, choiceIndex) => {
        insertChoice.run(questionId, choice, choiceIndex);
      });
    });

    res.json({ id: quizId.toString(), message: 'Quiz created successfully' });
  } catch (error) {
    console.error('Create quiz error:', error);
    res.status(500).json({ error: 'Failed to create quiz' });
  }
});

// Record quiz completion
router.post('/:quizId/complete', (req, res) => {
  const { quizId } = req.params;
  const { userId, score, totalQuestions, passed, xpEarned } = req.body;
  const db = req.app.locals.db;

  try {
    console.log(`📥 Quiz completion request: quizId=${quizId}, userId=${userId}, xpEarned=${xpEarned}`);
    
    // Check if already completed
    const existing = db.prepare('SELECT * FROM quiz_completions WHERE quiz_id = ? AND user_id = ?')
      .get(quizId, userId);

    if (existing) {
      console.log('⚠️ Quiz already completed, returning current user XP');
      // Return current user XP instead of error
      const currentProfile = db.prepare('SELECT xp, level FROM user_profiles WHERE user_id = ?').get(userId);
      return res.json({ 
        message: 'Quiz already completed',
        user: {
          xp: currentProfile?.xp || 0,
          level: currentProfile?.level || 1
        }
      });
    }

    // Record completion
    db.prepare(`
      INSERT INTO quiz_completions (quiz_id, user_id, score, total_questions, passed, xp_earned)
      VALUES (?, ?, ?, ?, ?, ?)
    `).run(quizId, userId, score, totalQuestions, passed ? 1 : 0, xpEarned || 0);

    // Update user XP if earned
    let updatedUser = null;
    if (xpEarned && xpEarned > 0) {
      console.log(`🎯 Updating XP for user ${userId}: +${xpEarned} XP`);
      const profile = db.prepare('SELECT xp FROM user_profiles WHERE user_id = ?').get(userId);
      const currentXP = profile?.xp || 0;
      const newXP = currentXP + xpEarned;
      const newLevel = calculateLevel(newXP);
      
      console.log(`📊 XP Update: ${currentXP} + ${xpEarned} = ${newXP} → Level ${newLevel}`);
      
      // Update only XP and level - created_at remains unchanged (immutable)
      db.prepare('UPDATE user_profiles SET xp = ?, level = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?')
        .run(newXP, newLevel, userId);
      
      // Save database immediately
      saveDatabase();
      console.log('✅ Database saved after quiz XP update');
      
      // Return updated user data
      const updatedProfile = db.prepare('SELECT xp, level FROM user_profiles WHERE user_id = ?').get(userId);
      updatedUser = {
        xp: updatedProfile.xp,
        level: updatedProfile.level
      };
      console.log('✅ Returning updated user:', updatedUser);
    }

    res.json({ 
      message: 'Quiz completion recorded',
      user: updatedUser
    });
  } catch (error) {
    console.error('Complete quiz error:', error);
    res.status(500).json({ error: 'Failed to record quiz completion' });
  }
});

export default router;

