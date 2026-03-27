# 🤖 AI Documentation Index

All AI agent documentation is centralized here for easy reference.

## 📚 Quick Reference

| File | Purpose |
|------|---------|
| [AGENTS.md](AGENTS.md) | Project conventions, workflows, and development guide |
| [RULES.md](RULES.md) | Coding standards and project rules |
| [SKILLS.md](SKILLS.md) | Technical skills and capabilities reference |

## 🎯 For Human Developers

This `.ai/` folder contains documentation specifically for AI agents working on this project. 

**For human developers**, please see:
- [README.md](../README.md) - Project overview
- [.github/PULL_REQUEST_TEMPLATE.md](../.github/PULL_REQUEST_TEMPLATE.md) - PR guidelines

## 🔄 How AI Agents Use This

1. **Before starting work** - Read AGENTS.md for project conventions
2. **Before coding** - Check RULES.md for standards
3. **Before creating PR** - Follow SKILLS.md and PR template

## 📝 Pre-PR Requirements

All AI agents MUST complete before creating PR:
1. ✅ Clean Compile (`mvn clean compile -q`)
2. ✅ Run Tests (`mvn test`)
3. ✅ Start Application (`mvn spring-boot:run`)
4. ✅ Manual Testing
5. ✅ Verify No Breaking Changes

See [AGENTS.md](AGENTS.md) for details.

---

*This documentation helps AI agents understand the project and maintain code quality.*
