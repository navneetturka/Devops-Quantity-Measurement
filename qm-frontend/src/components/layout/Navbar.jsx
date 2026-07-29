import { useEffect, useRef, useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { HiOutlineMenu, HiOutlineX } from "react-icons/hi";
import { FiLogOut, FiSun, FiMoon, FiChevronDown } from "react-icons/fi";
import { useAuth } from "../../hooks/useAuth";
import { useTheme } from "../../hooks/useTheme";
import "./Navbar.css";

export default function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const { theme, toggleTheme } = useTheme();
  const [open, setOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const profileRef = useRef(null);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    setOpen(false);
    setProfileOpen(false);
    navigate("/");
  };

  // Close the profile dropdown on outside click or Escape.
  useEffect(() => {
    if (!profileOpen) return;
    function onPointerDown(e) {
      if (profileRef.current && !profileRef.current.contains(e.target)) {
        setProfileOpen(false);
      }
    }
    function onKeyDown(e) {
      if (e.key === "Escape") setProfileOpen(false);
    }
    document.addEventListener("mousedown", onPointerDown);
    document.addEventListener("keydown", onKeyDown);
    return () => {
      document.removeEventListener("mousedown", onPointerDown);
      document.removeEventListener("keydown", onKeyDown);
    };
  }, [profileOpen]);

  const linkClass = ({ isActive }) => "nav-link" + (isActive ? " nav-link-active" : "");

  return (
    <header className="navbar">
      <div className="page navbar-inner">
        <Link to="/" className="brand" onClick={() => setOpen(false)}>
          <span className="brand-mark">QM</span>
          <span className="brand-name">Quantity Measurement</span>
        </Link>

        <nav className={`nav-links ${open ? "nav-links-open" : ""}`}>
          {!isAuthenticated && (
            <>
              <NavLink to="/" className={linkClass} end onClick={() => setOpen(false)}>
                Home
              </NavLink>
              <NavLink to="/about" className={linkClass} onClick={() => setOpen(false)}>
                About
              </NavLink>
              <Link to="/login" className="btn btn-primary nav-cta" onClick={() => setOpen(false)}>
                Login
              </Link>
            </>
          )}

          {isAuthenticated && (
            <>
              <NavLink to="/dashboard" className={linkClass} onClick={() => setOpen(false)}>
                Dashboard
              </NavLink>
              <NavLink to="/history" className={linkClass} onClick={() => setOpen(false)}>
                History
              </NavLink>

              <div className="nav-profile-wrap" ref={profileRef}>
                <button
                  type="button"
                  className="nav-profile-trigger"
                  onClick={() => setProfileOpen((v) => !v)}
                  aria-haspopup="menu"
                  aria-expanded={profileOpen}
                  aria-label="Open profile menu"
                >
                  {user?.picture ? (
                    <img
                      src={user.picture}
                      alt={user?.name || "Profile"}
                      className="nav-avatar"
                      referrerPolicy="no-referrer"
                    />
                  ) : (
                    <div className="nav-avatar nav-avatar-fallback">{(user?.name || "U")[0]}</div>
                  )}
                  <FiChevronDown className={"nav-profile-chevron" + (profileOpen ? " nav-profile-chevron-open" : "")} size={14} />
                </button>

                {profileOpen && (
                  <div className="nav-profile-dropdown" role="menu">
                    <div className="nav-profile-dropdown-header">
                      {user?.picture ? (
                        <img
                          src={user.picture}
                          alt={user?.name || "Profile"}
                          className="nav-profile-dropdown-avatar"
                          referrerPolicy="no-referrer"
                        />
                      ) : (
                        <div className="nav-profile-dropdown-avatar nav-avatar-fallback">
                          {(user?.name || "U")[0]}
                        </div>
                      )}
                      <div className="nav-profile-dropdown-meta">
                        <p className="nav-profile-dropdown-name">{user?.name || "there"}</p>
                        <p className="nav-profile-dropdown-email mono">{user?.email}</p>
                      </div>
                    </div>
                    <button
                      type="button"
                      className="nav-profile-dropdown-logout"
                      onClick={handleLogout}
                      role="menuitem"
                    >
                      <FiLogOut /> Logout
                    </button>
                  </div>
                )}
              </div>
            </>
          )}

          <button
            className="btn btn-ghost btn-icon theme-toggle"
            onClick={toggleTheme}
            aria-label={theme === "light" ? "Switch to dark mode" : "Switch to light mode"}
            title={theme === "light" ? "Switch to dark mode" : "Switch to light mode"}
          >
            {theme === "light" ? <FiMoon size={17} /> : <FiSun size={17} />}
          </button>
        </nav>

        <button className="nav-toggle" onClick={() => setOpen((v) => !v)} aria-label="Toggle menu">
          {open ? <HiOutlineX size={22} /> : <HiOutlineMenu size={22} />}
        </button>
      </div>
    </header>
  );
}
