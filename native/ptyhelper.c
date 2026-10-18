/* ptyhelper PIDFILE SIZEFILE COLS ROWS -- CMD [ARGS...]
 * Runs CMD on a fresh pty. stdin/stdout relay to the pty master.
 * SIGUSR1 => re-read "COLS ROWS" from SIZEFILE and resize the pty. */
#define _GNU_SOURCE
#include <errno.h>
#include <fcntl.h>
#include <poll.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/ioctl.h>
#include <sys/wait.h>
#include <termios.h>
#include <unistd.h>

static volatile sig_atomic_t resize_flag;
static void on_usr1(int s) { (void)s; resize_flag = 1; }

static void set_size(int fd, int cols, int rows) {
    struct winsize ws = {(unsigned short)rows, (unsigned short)cols, 0, 0};
    ioctl(fd, TIOCSWINSZ, &ws);
}

static void apply_resize(int m, const char *file) {
    int c, r;
    FILE *f = fopen(file, "r");
    if (f && fscanf(f, "%d %d", &c, &r) == 2) set_size(m, c, r);
    if (f) fclose(f);
}

/* Copy one chunk; 0 = keep going, -1 = EOF/error. */
static int relay(int from, int to) {
    char buf[8192];
    ssize_t n = read(from, buf, sizeof buf);
    if (n < 0) return errno == EINTR || errno == EAGAIN ? 0 : -1;
    if (n == 0) return -1;
    for (ssize_t o = 0; o < n;) {
        ssize_t w = write(to, buf + o, n - o);
        if (w < 0) { if (errno == EINTR) continue; return -1; }
        o += w;
    }
    return 0;
}

static void child(int m, const char *slave, char **argv) {
    setsid();
    int s = open(slave, O_RDWR);
    if (s < 0) _exit(126);
    ioctl(s, TIOCSCTTY, 0);
    dup2(s, 0); dup2(s, 1); dup2(s, 2);
    if (s > 2) close(s);
    close(m);
    execvp(argv[0], argv);
    perror("ptyhelper: exec");
    _exit(127);
}

int main(int argc, char **argv) {
    if (argc < 7 || strcmp(argv[5], "--")) { fputs("usage: ptyhelper PID SIZE COLS ROWS -- CMD...\n", stderr); return 2; }
    int m = open("/dev/ptmx", O_RDWR | O_NOCTTY);
    if (m < 0 || grantpt(m) || unlockpt(m)) { perror("ptyhelper: ptmx"); return 1; }
    char *slave = ptsname(m);
    struct termios t;
    if (tcgetattr(m, &t) == 0) { t.c_iflag |= IUTF8; tcsetattr(m, TCSANOW, &t); }
    set_size(m, atoi(argv[3]), atoi(argv[4]));
    FILE *pf = fopen(argv[1], "w");
    if (pf) { fprintf(pf, "%d", getpid()); fclose(pf); }
    struct sigaction sa;
    memset(&sa, 0, sizeof sa);
    sa.sa_handler = on_usr1;
    sigaction(SIGUSR1, &sa, NULL);
    pid_t pid = fork();
    if (pid == 0) child(m, slave, argv + 6);
    struct pollfd fds[2] = {{0, POLLIN, 0}, {m, POLLIN, 0}};
    for (;;) {
        if (resize_flag) { resize_flag = 0; apply_resize(m, argv[2]); }
        if (poll(fds, 2, -1) < 0) continue;
        if (fds[1].revents && relay(m, 1) < 0) break;
        if (fds[0].revents && relay(0, m) < 0) fds[0].fd = -1;
    }
    int st = 0;
    waitpid(pid, &st, 0);
    return WIFEXITED(st) ? WEXITSTATUS(st) : 128 + WTERMSIG(st);
}
