## 📋 Pull Request Template

Use this template when creating pull requests for this project.

### 🎯 PR Title Format
```
[type]: [short description]

Examples:
✨ feat: add rate limiting with bucket4j
🐛 fix: correct inverted boolean logic
📝 docs: update configuration examples
♻️ refactor: extract utility methods
🔧 chore: update dependencies
```

### 🏷️ Types
- ✨ `feat`: New feature
- 🐛 `fix`: Bug fix
- 📝 `docs`: Documentation changes
- 💎 `style`: Code style (formatting, semicolons, etc)
- ♻️ `refactor`: Code refactoring
- ⚡ `perf`: Performance improvements
- 🧪 `test`: Tests
- 🔧 `chore`: Build process, CI/CD, dependencies
- 🛡️ `security`: Security fixes

---

## 🎯 Summary
<!-- Briefly describe what this PR does -->

## 🔄 Changes Made
<!-- List the specific changes -->
- [ ] Change 1
- [ ] Change 2
- [ ] Change 3

## 📊 Testing Performed
<!-- Check all that apply -->
- [ ] Step 1: Clean Compile (`mvn clean compile -q`)
- [ ] Step 2: Run Tests (`mvn test`)
- [ ] Step 3: Start Application (`mvn spring-boot:run`)
- [ ] Step 4: Manual Testing
  - [ ] Tested modified endpoints
  - [ ] Verified rate limiting
  - [ ] Checked error responses
  - [ ] Tested success/failure cases
- [ ] Step 5: No Breaking Changes

## 📝 Test Results
```bash
# Compile
$ mvn clean compile -q
✅ Result: [SUCCESS/FAILED]

# Tests
$ mvn test
✅ Result: [X tests passed / FAILED]

# Application Startup
$ mvn spring-boot:run
✅ Result: [Started in X seconds / FAILED]

# Manual Testing
Endpoint: GET /hello
✅ Result: [HTTP 200 / 429] - "Hello, World!"
```

## 🎨 Screenshots / Evidence
<!-- If applicable, add screenshots or output logs -->

## ⚠️ Breaking Changes
<!-- If any, describe breaking changes and migration steps -->
- [ ] No breaking changes
- [ ] Breaking changes (describe below)

## 📚 Additional Notes
<!-- Any additional information for reviewers -->

## ✅ Checklist
- [ ] Code follows project conventions (from AGENTS.md)
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated (if needed)
- [ ] No commented-out code
- [ ] No hardcoded values (use properties)
- [ ] Proper error handling implemented
- [ ] Logging added where appropriate

---

## 🎨 Creative PR Title Examples

### Good Titles
```
✨ feat: add hybrid rate limiting with filter + service layers
🐛 fix: prevent memory leak in ConcurrentHashMap
⚡ perf: replace UUID with ThreadLocalRandom (10x faster)
♻️ refactor: extract fast ID generation to StringUtils
📝 docs: add configuration properties examples
```

### Bad Titles
```
Update code
Fix bug
Add stuff
Changes
```

## 🏆 PR Description Best Practices

1. **Be Clear**: Explain WHAT and WHY, not just HOW
2. **Be Specific**: Include actual commands and outputs
3. **Be Honest**: If tests fail, explain why
4. **Be Complete**: Fill out all sections
5. **Be Creative**: Use emojis to make it readable

## 🚀 Example Complete PR

```markdown
## 🎯 Summary
Added Bucket4j for production-ready rate limiting with token bucket algorithm.

## 🔄 Changes Made
- ✨ Added Bucket4j dependency (v8.17.0)
- ✨ Replaced custom rate limiter with Bucket4j
- ✨ Added configuration properties support
- ✨ Created service layer for per-endpoint limits

## 📊 Testing Performed
- [x] Step 1: Clean Compile
- [x] Step 2: Run Tests  
- [x] Step 3: Start Application
- [x] Step 4: Manual Testing
- [x] Step 5: No Breaking Changes

## 📝 Test Results
```bash
$ mvn clean compile -q
✅ SUCCESS

$ mvn spring-boot:run
✅ Started in 1.183 seconds

$ curl http://localhost:8080/hello
✅ "Hello, World!"

# Rate Limiting Test
$ for i in {1..12}; do curl http://localhost:8080/hello; done
✅ Requests 1-10: HTTP 200
✅ Requests 11-12: HTTP 429
```

## ⚠️ Breaking Changes
- [x] No breaking changes

## 📚 Additional Notes
Performance improved:
- Before: Custom sliding window
- After: Token bucket (Bucket4j)
- Result: Better memory efficiency & throughput

## ✅ Checklist
- [x] All items checked
```

---

**Remember: A good PR makes reviewer's life easy!** 🎉
